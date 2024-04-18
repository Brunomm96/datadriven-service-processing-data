package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.config.TenantContext;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.LossMonitoringOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.LossMonitoringOutOfBoundResult;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.LossMonitoringCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.LossMonitoringOutOfBoundRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.NotificationCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.schedule.ScheduleLossMonitoringProperties;
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
public class LossMonitoringService implements MonitoringService {

    @Getter
    @Setter
    private String prefix = "[LOSS_MONITORING]";

    @Getter
    @Autowired
    ScheduleLossMonitoringProperties scheduleLossMonitoringProperties;

    @Autowired
    NotificationCustomRepository notificationCustomRepository;

    @Autowired
    LossMonitoringCustomRepository lossMonitoringMonitoringCustomRepository;

    @Autowired
    LossMonitoringOutOfBoundRepository lossMonitoringOutOfBoundRepository;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    WebClient webClientTelegram;

    @Override
    public boolean isCanExecService() {

        String[] unidadeProducao = scheduleLossMonitoringProperties.getProductionUnit();

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

        if (isCanExecService()) {
            execCalc();
        }

    }

    private void execCalc() {

        String[] dbBiProperties = scheduleLossMonitoringProperties.getDbBiProductionUnit();
        String[] dbAppProperties = scheduleLossMonitoringProperties.getDbAppProductionUnit();
        String[] productionUnit = scheduleLossMonitoringProperties.getProductionUnit();

        for (int i = 0; i < dbBiProperties.length; i++) {

            String dbBI = dbBiProperties[i];
            String dbApp = dbAppProperties[i];

            logger("Consultando dados da unidade: " + productionUnit[i]);

            TenantContext.setCurrentTenant(dbApp);

            // Monitoramento de refugo por tipo de refigo
            List<LossMonitoringOutOfBoundResult> lossMonitoringsOutOfBound = lossMonitoringMonitoringCustomRepository.listLossMonitoringOutOfBound(dbBI, productionUnit[i]);

            if (lossMonitoringsOutOfBound != null && lossMonitoringsOutOfBound.size() > 0)
                lossMonitoringsOutOfBound.forEach(lossMonitoringOutOfBoundResult -> notifyLossMonitoringOutOfBound(dbApp, lossMonitoringOutOfBoundResult));
            else
                logger("[MONITORAMENTO DE REFUGO POR TIPO DE REFUGO] - Não foi encontrado dados a serem processados na unidade: " + productionUnit[i]);

            // Monitoramento de refugo por peca
            List<LossMonitoringOutOfBoundResult> lossMonitoringsOutOfBoundByProduct = lossMonitoringMonitoringCustomRepository.listLossMonitoringOutOfBoundByProduct(dbBI, productionUnit[i]);

            if (lossMonitoringsOutOfBoundByProduct != null && lossMonitoringsOutOfBoundByProduct.size() > 0)
                lossMonitoringsOutOfBoundByProduct.forEach(lossMonitoringOutOfBoundResultByProduct -> notifyLossMonitoringOutOfBound(dbApp, lossMonitoringOutOfBoundResultByProduct));
            else
                logger("[MONITORAMENTO DE REFUGO POR PRODUTO] - Não foi encontrado dados a serem processados na unidade: " + productionUnit[i]);

        }
    }

    private void notifyLossMonitoringOutOfBound(String db, LossMonitoringOutOfBoundResult lossMonitoringMonitoringResult) {

        StringBuilder sb = new StringBuilder();
        sb.append("Identificado não conformidade de refugo, conforme informações abaixo:");
        sb.append("<br>");
        sb.append("<br>");
        sb.append("<b>Data do Apontamento: </b>");
        sb.append(lossMonitoringMonitoringResult.getDateAppointed());
        sb.append("<br>");
        sb.append("<b>OP: </b>");
        sb.append(lossMonitoringMonitoringResult.getProductionOrder());
        sb.append("<br>");
        sb.append("<b>Peça: </b>");
        sb.append(lossMonitoringMonitoringResult.getProductCode());
        sb.append("<br>");
        sb.append("<b>Quantidade Refugada: </b>");
        sb.append(lossMonitoringMonitoringResult.getLossQuantity());
        sb.append("<br>");
        sb.append("<b>Quantidade Tolerância: </b>");
        sb.append(lossMonitoringMonitoringResult.getToleranceQuantity());
        sb.append("<br>");

        if (lossMonitoringMonitoringResult.getRefugoId() != null) {
            sb.append("<b>Refugo: </b>");
            sb.append(lossMonitoringMonitoringResult.getLossCode() + " - " + lossMonitoringMonitoringResult.getLossName());
            sb.append("<br>");
        }

        notificationCustomRepository.listNotificationEmail(db, lossMonitoringMonitoringResult.getHelpChainId()).forEach(notificationEmail -> DatadrivenUtils.sendMail(sendMailService, notificationEmail, sb.toString()));
        notificationCustomRepository.listNotificationTelegram(db, lossMonitoringMonitoringResult.getHelpChainId()).forEach(notificationTelegram -> DatadrivenUtils.sendTelegram(webClientTelegram, notificationTelegram, sb.toString()));

        saveByLossMonitoringResult(lossMonitoringMonitoringResult);

    }

    private void saveByLossMonitoringResult(LossMonitoringOutOfBoundResult lossMonitoringMonitoringResult) {

        LossMonitoringOutOfBound lossMonitoringOutOfBound = new LossMonitoringOutOfBound();
        lossMonitoringOutOfBound.setProductionUnitId(lossMonitoringMonitoringResult.getProductionUnitId());
        lossMonitoringOutOfBound.setLossQuantity(lossMonitoringMonitoringResult.getLossQuantity());
        lossMonitoringOutOfBound.setAppointmentDate(lossMonitoringMonitoringResult.getDateAppointed());
        lossMonitoringOutOfBound.setHelpChainId(lossMonitoringMonitoringResult.getHelpChainId());
        lossMonitoringOutOfBound.setProductCode(lossMonitoringMonitoringResult.getProductCode());
        lossMonitoringOutOfBound.setProductionOrderId(lossMonitoringMonitoringResult.getOpId());
        lossMonitoringOutOfBound.setRefugoId(lossMonitoringMonitoringResult.getRefugoId());
        lossMonitoringOutOfBound.setToleranceQuantity(lossMonitoringMonitoringResult.getToleranceQuantity());

        lossMonitoringOutOfBoundRepository.save(lossMonitoringOutOfBound);

    }

}
