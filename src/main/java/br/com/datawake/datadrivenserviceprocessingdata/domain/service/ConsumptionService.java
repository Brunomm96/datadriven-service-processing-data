package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.config.TenantContext;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBoundByProduct;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.ConsumptionMonitoringCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.NotificationCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.schedule.ScheduleConsumptionProperties;
import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class ConsumptionService implements MonitoringService {

    @Getter
    @Setter
    private String prefix = "[CONSUMPTION]";

    @Getter
    @Autowired
    ScheduleConsumptionProperties scheduleConsumptionProperties;

    @Autowired
    NotificationCustomRepository notificationCustomRepository;

    @Autowired
    ConsumptionMonitoringCustomRepository consumptionMonitoringCustomRepository;

    @Autowired
    ConsumptionMaterialOutOfBoundService consumptionMaterialOutOfBoundService;

    @Autowired
    ConsumptionMaterialByProductOutOfBoundService consumptionMaterialByProductOutOfBoundService;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    WebClient webClientTelegram;

    @Override
    public boolean isCanExecService() {

        String[] unidadeProducao = scheduleConsumptionProperties.getProductionUnit();

        if (unidadeProducao.length > 0) {
            return true;
        }

        logger("Monitoramento de consumo de materiais não pode ser processado pois não está configurado no schedule.");

        return false;
    }

    @Override
    public void logger(String message) {
        log.info(this.prefix + " - " + message);
    }

    @Override
    public void execProcData() {

        logger("Verificando dados...");

        if ( isCanExecService() ) {
            execCalc();
        }

    }

    private void execCalc() {

        String[] dbBiProperties = scheduleConsumptionProperties.getDbBiProductionUnit();
        String[] dbAppProperties = scheduleConsumptionProperties.getDbAppProductionUnit();
        String[] productionUnit = scheduleConsumptionProperties.getProductionUnit();

        for (int i = 0; i < dbBiProperties.length; i++) {

            String dbBI = dbBiProperties[i];
            String dbApp = dbAppProperties[i];

            logger("Consultando dados da unidade: " + productionUnit[i]);

            TenantContext.setCurrentTenant(dbApp);

            // Monitoramento de consumo por materia prima
            List<ConsumptionOutOfBound> consumptionsOutOfBound = consumptionMonitoringCustomRepository.listConsumptionsOutOfBound(dbBI, productionUnit[i]);

            if (consumptionsOutOfBound != null && consumptionsOutOfBound.size() > 0)
                consumptionsOutOfBound.forEach(consumptionOutOfBound -> notifyConsumptionOutOfBound(dbApp, consumptionOutOfBound));
            else
                logger("[MONITORAMENTO DE CONSUMO POR MATERIA PRIMA] - Não foi encontrado dados a serem processados na unidade: " + productionUnit[i]);

            // Monitoramento de consumo por peca
            List<ConsumptionOutOfBoundByProduct> consumptionsOutOfBoundByProduct = consumptionMonitoringCustomRepository.listConsumptionsOutOfBoundByProduct(dbBI, productionUnit[i]);

            if (consumptionsOutOfBoundByProduct != null && consumptionsOutOfBoundByProduct.size() > 0)
                consumptionsOutOfBoundByProduct.forEach(consumptionOutOfBoundByProduct -> notifyConsumptionOutOfBoundByProduct(dbApp, consumptionOutOfBoundByProduct));
            else
                logger("[MONITORAMENTO DE CONSUMO POR PRODUTO] - Não foi encontrado dados a serem processados na unidade: " + productionUnit[i]);

        }
    }

    private void notifyConsumptionOutOfBoundByProduct(String db, ConsumptionOutOfBoundByProduct consumptionMonitoringByProductResult) {

        StringBuilder sb = new StringBuilder();
        sb.append("Identificado não conformidade de consumo de materiais, conforme informações abaixo:");
        sb.append("<br>");
        sb.append("<br>");
        sb.append("<b>Data </b>");
        sb.append(consumptionMonitoringByProductResult.getDate());
        sb.append("<br>");
        sb.append("<b>OP: </b>");
        sb.append(consumptionMonitoringByProductResult.getOp());
        sb.append("<br>");
        sb.append("<b>Quantidade Total Planejada da OP: </b>");
        sb.append(String.valueOf( consumptionMonitoringByProductResult.getAmountRevenue() ) );
        sb.append("<br>");
        sb.append("<b>Peça: </b>");
        sb.append(consumptionMonitoringByProductResult.getProductCode());
        sb.append("<br>");
        sb.append(consumptionMonitoringByProductResult.getProductDescription());
        sb.append("<br>");
        sb.append("<b>Peso Planejado: </b>");
        sb.append(consumptionMonitoringByProductResult.getQuantityPerPiece());
        sb.append("<br>");
        sb.append("<b>Peso Real: </b>");
        sb.append(consumptionMonitoringByProductResult.getAppointedAmount());
        sb.append("<br>");
        sb.append("<b>Tolerância Mínima: </b>");
        sb.append(consumptionMonitoringByProductResult.getToleranceMin());
        sb.append("<br>");
        sb.append("<b>Tolerância Máxima: </b>");
        sb.append(consumptionMonitoringByProductResult.getToleranceMax());
        sb.append("<br>");

        notificationCustomRepository.listNotificationEmail(db, consumptionMonitoringByProductResult.getHelpChainId()).forEach(notificationEmail -> DatadrivenUtils.sendMail(sendMailService, notificationEmail, sb.toString()));
        notificationCustomRepository.listNotificationTelegram(db, consumptionMonitoringByProductResult.getHelpChainId()).forEach(notificationTelegram -> DatadrivenUtils.sendTelegram(webClientTelegram, notificationTelegram, sb.toString()));

        consumptionMaterialByProductOutOfBoundService.saveByConsumptionOutOfBoundByProduct(consumptionMonitoringByProductResult);
    }

    private void notifyConsumptionOutOfBound(String db, ConsumptionOutOfBound consumptionMonitoringResult) {

        StringBuilder sb = new StringBuilder();
        sb.append("Identificado não conformidade de consumo de materiais, conforme informações abaixo:");
        sb.append("<br>");
        sb.append("<br>");
        sb.append("<b>Data/Hora do Apontamento: </b>");
        sb.append(consumptionMonitoringResult.getDateHourAppointed());
        sb.append("<br>");
        sb.append("<b>OP: </b>");
        sb.append(consumptionMonitoringResult.getOp());
        sb.append("<br>");
        sb.append("<b>Quantidade Total Planejada da OP: </b>");
        sb.append(String.valueOf( consumptionMonitoringResult.getAmountRevenue() ) );
        sb.append("<br>");
        sb.append("<b>Peça: </b>");
        sb.append(consumptionMonitoringResult.getProductCode());
        sb.append("<br>");
        sb.append("<b>Material: </b>");
        sb.append(consumptionMonitoringResult.getComponentCode());
        sb.append("<br>");
        sb.append("<b>Quantidade Receita (p/ 1 Peça): </b>");
        sb.append(consumptionMonitoringResult.getQuantityPerPiece());
        sb.append("<br>");
        sb.append("<b>Quantidade Apontada (p/ 1 Peça): </b>");
        sb.append(consumptionMonitoringResult.getAppointedAmount());
        sb.append("<br>");
        sb.append("<b>Tolerância Mínima: </b>");
        sb.append(consumptionMonitoringResult.getToleranceMin());
        sb.append("<br>");
        sb.append("<b>Tolerância Máxima: </b>");
        sb.append(consumptionMonitoringResult.getToleranceMax());
        sb.append("<br>");

        notificationCustomRepository.listNotificationEmail(db, consumptionMonitoringResult.getHelpChainId()).forEach(notificationEmail -> DatadrivenUtils.sendMail(sendMailService, notificationEmail, sb.toString()));
        notificationCustomRepository.listNotificationTelegram(db, consumptionMonitoringResult.getHelpChainId()).forEach(notificationTelegram -> DatadrivenUtils.sendTelegram(webClientTelegram, notificationTelegram, sb.toString()));

        consumptionMaterialOutOfBoundService.saveByConsumptionOutOfBound(consumptionMonitoringResult);

    }



}
