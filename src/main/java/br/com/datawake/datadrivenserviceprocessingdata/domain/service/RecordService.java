package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.RecordCustomRepository;
import br.com.datawake.datadrivenserviceprocessingdata.domain.schedule.ScheduleRecordProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RecordService implements MonitoringService {

    @Getter
    @Setter
    private String prefix = "[RECORDS]";

    @Getter
    @Autowired
    ScheduleRecordProperties scheduleRecordProperties;

    @Autowired
    RecordCustomRepository recordCustomRepository;

    @Override
    public boolean isCanExecService() {

        String[] unidadeProducao = scheduleRecordProperties.getProductionUnit();

        if ( unidadeProducao.length > 0) {
            return true;
        }

        logger("Processamento de cadastros não foi realizado pois não está nas parametrizado no schedule.");

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

       String[] dbBiProperties = scheduleRecordProperties.getDbBiProductionUnit();
       String[] productionUnit = scheduleRecordProperties.getProductionUnit();

       for (int i = 0; i < dbBiProperties.length; i++) {
            String dbBi = dbBiProperties[i];
            logger("Processando unidade: " + productionUnit[i]);
           recordCustomRepository.execProcRecord(dbBi,productionUnit[i]);
        }
    }
}
