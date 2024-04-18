package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.config.TenantContext;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConfigGoalProduct;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.MetasNotify;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.MetasNotifyModel;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.ConfigGoalProductRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.MetasCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.MetasNotifyRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.NotificationCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.schedule.ScheduleMetasProperties;
import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Slf4j
@Service
public class MetaService implements MonitoringService {

    @Getter
    @Setter
    private String prefix = "[METAS]";

    @Getter
    @Autowired
    ScheduleMetasProperties scheduleMetasProperties;

    @Autowired
    MetasCustomRepository metasCustomRepository;

    @Autowired
    ConfigGoalProductRepository configGoalProductRepository;

    @Autowired
    MetasNotifyRepository metasNotifyRepository;

    @Autowired
    NotificationCustomRepository notificationCustomRepository;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    WebClient webClientTelegram;


    @Override
    public boolean isCanExecService() {

        String hourProcessMeta = scheduleMetasProperties.getHourDayStart();
        String[] unidadeProducao = scheduleMetasProperties.getProductionUnit();

        LocalTime timeNow = LocalTime.now();
        int hour = timeNow.getHour();
        int minute = timeNow.getMinute();

        if (    hourProcessMeta.length() == 5 &&
                hour == Integer.valueOf(hourProcessMeta.substring(0,2)) &&
                minute == Integer.valueOf(hourProcessMeta.substring(3,5) ) &&
                unidadeProducao.length > 0) {
            return true;
        }

        logger("Processamento não foi realizado pois não está nas condições parametrizadas do schedule. Todo dia as [" + hour + ":" + minute + "].");

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

        String[] dbBiProperties = scheduleMetasProperties.getDbBiProductionUnit();
        String[] dbAppProperties = scheduleMetasProperties.getDbAppProductionUnit();
        String[] productionUnit = scheduleMetasProperties.getProductionUnit();

        for (int i = 0; i < dbBiProperties.length; i++) {
            String dbBI = dbBiProperties[i];
            String dbApp = dbAppProperties[i];
            logger("Processando unidade: " + productionUnit[i]);

            TenantContext.setCurrentTenant(dbApp);

            metasCustomRepository.execProcMetas(dbBI, productionUnit[i]);
            metasCustomRepository.execMetasNotify(dbBI, productionUnit[i]).forEach(metasNotify -> sendNotifyMetas(dbApp, metasNotify));

        }
    }

    private void sendNotifyMetas(String db, MetasNotifyModel metasNotifyModel) {

        StringBuilder sb = new StringBuilder();
        sb.append("Identificado melhor performance de meta:");
        sb.append("<br>");
        sb.append("<br>");
        sb.append("<b>Unidade de Produção: </b>");
        sb.append(metasNotifyModel.getProductionUnitCode() + " - " + metasNotifyModel.getProductionUnitName());
        sb.append("<br>");
        sb.append("<b>Produto: </b>");
        sb.append(metasNotifyModel.getProductCode() + " - " + metasNotifyModel.getProductName());
        sb.append("<br>");
        sb.append("<b>Meta atual: </b>");
        sb.append(metasNotifyModel.getCurrentProductPieceHour());
        sb.append("<br>");
        sb.append("<b>Nova meta: </b>");
        sb.append(metasNotifyModel.getProductNewPieceHour());
        sb.append("<br>");

        notificationCustomRepository.listNotificationEmail(db, metasNotifyModel.getHelpChainId()).forEach(notificationEmail -> DatadrivenUtils.sendMail(sendMailService, notificationEmail, sb.toString()));
        notificationCustomRepository.listNotificationTelegram(db, metasNotifyModel.getHelpChainId()).forEach(notificationTelegram -> DatadrivenUtils.sendTelegram(webClientTelegram, notificationTelegram, sb.toString()));

        saveMetasNotify(metasNotifyModel);

    }

    @Transactional
    private MetasNotify saveMetasNotify(MetasNotifyModel metasNotifyModel) {

        ConfigGoalProduct configGoalProduct = configGoalProductRepository.getReferenceById(metasNotifyModel.getConfigGoalProductId());
        MetasNotify metasNotify = new MetasNotify();

        if ( configGoalProduct != null && configGoalProduct.getId() > 0 ) {


            metasNotify.setMetaId(metasNotifyModel.getMetaId());
            metasNotify.setCurrentProductPieceHour(metasNotifyModel.getCurrentProductPieceHour());
            metasNotify.setDateNotify(OffsetDateTime.now());
            metasNotify.setProductCode(metasNotifyModel.getProductCode());
            metasNotify.setHelpChainId((metasNotifyModel.getHelpChainId() == 0) ? null :metasNotifyModel.getHelpChainId() );
            metasNotify.setProductNewPieceHour(metasNotifyModel.getProductNewPieceHour());
            metasNotify.setConfigGoalProduct(configGoalProduct);
            metasNotify.setProductionUnitId(metasNotifyModel.getProductionUnitId());
            metasNotify = metasNotifyRepository.save(metasNotify);

        }

        return metasNotify;
    }
}
