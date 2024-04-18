package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.config.TenantContext;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.ConfigGoalProductRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.MetasNotifyRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.NotificationCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.ProcessTemplatesCsvRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.schedule.ScheduleTemplatesCsvProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class TemplatesCsvService implements MonitoringService {

    @Getter
    @Setter
    private String prefix = "[TEMPLATES_CSV]";

    @Getter
    @Autowired
    ScheduleTemplatesCsvProperties scheduleTemplatesCsvProperties;

    @Autowired
    ProcessTemplatesCsvRepository processTemplatesCsvRepository;


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

        String[] dbAppTemplatesCsv = scheduleTemplatesCsvProperties.getDbAppTemplatesCsv();

        if ( dbAppTemplatesCsv.length > 0 ) {
            return true;
        }

        logger("Processamento dos templates de CSV não pode ser processado pois não está configurado no schedule.");

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

        String[] dbAppTemplatesCsv = scheduleTemplatesCsvProperties.getDbAppTemplatesCsv();

        for (int i = 0; i < dbAppTemplatesCsv.length; i++) {
            String dbApp = dbAppTemplatesCsv[i];

            logger("Processando templates CSV: " + dbApp );

            TenantContext.setCurrentTenant(dbApp);

            processTemplatesCsvRepository.execTemplatesPending( dbApp );

        }
    }


}
