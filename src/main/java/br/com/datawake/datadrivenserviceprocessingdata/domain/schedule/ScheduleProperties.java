package br.com.datawake.datadrivenserviceprocessingdata.domain.schedule;

public interface ScheduleProperties {

    String getFrequency();
    String[] getProductionUnit();
    String[] getDbBiProductionUnit();
    String[] getDbAppProductionUnit();

}
