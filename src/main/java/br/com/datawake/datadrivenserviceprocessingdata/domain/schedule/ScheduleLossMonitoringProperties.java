package br.com.datawake.datadrivenserviceprocessingdata.domain.schedule;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@Component
@ConfigurationProperties("schedule.loss-monitoring")
public class ScheduleLossMonitoringProperties implements ScheduleProperties {

    @NotNull
    private String frequency;

    @NotNull
    private String[] productionUnit;

    @NotNull
    private String[] dbBiProductionUnit;

    @NotNull
    private String[] dbAppProductionUnit;

}
