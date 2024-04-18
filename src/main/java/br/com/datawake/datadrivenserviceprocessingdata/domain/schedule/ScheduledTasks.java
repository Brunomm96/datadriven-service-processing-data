package br.com.datawake.datadrivenserviceprocessingdata.domain.schedule;

import br.com.datawake.datadrivenserviceprocessingdata.domain.service.ConsumptionService;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.LossMonitoringService;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.MetaService;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.RecordService;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.TemplatesCsvService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTasks {

        @Autowired
        ApplicationContext context;

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        MetaService metaService;

        @Autowired
        ConsumptionService consumptionService;

        @Autowired
        RecordService recordService;

        @Autowired
        LossMonitoringService lossMonitoringService;

        @Autowired
        TemplatesCsvService templatesCsvService;

        @Scheduled(
                timeUnit = TimeUnit.SECONDS,
                fixedRateString = "${schedule.metas.frequency:10}"
        )
        public void execTaskMetas() { metaService.execProcData(); }

        @Scheduled(
                timeUnit = TimeUnit.SECONDS,
                fixedRateString = "${schedule.record.frequency:10}"
        )
        public void execTaskRecord() { recordService.execProcData(); }

        @Scheduled(
                timeUnit = TimeUnit.SECONDS,
                fixedRateString = "${schedule.consumption.frequency:15}"
        )
        public void execTaskConsumption() {
                consumptionService.execProcData();
        }

        @Scheduled(
                timeUnit = TimeUnit.SECONDS,
                fixedRateString = "${schedule.loss-monitoring.frequency:15}"
        )
        public void execTaskLossMonitoring() {
                lossMonitoringService.execProcData();
        }

        @Scheduled(
                timeUnit = TimeUnit.SECONDS,
                fixedRateString = "${schedule.process-templates-csv.frequency:15}"
        )
        public void execprocessTemplatesCsv() {
                templatesCsvService.execProcData();
        }
}
