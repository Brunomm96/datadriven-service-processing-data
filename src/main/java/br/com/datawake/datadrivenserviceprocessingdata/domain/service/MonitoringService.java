package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.schedule.ScheduleProperties;

public interface MonitoringService {

    String getPrefix();
    void setPrefix(String prefix);
    boolean isCanExecService();
    void logger(String message);
    void execProcData();

}
