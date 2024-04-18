package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LossMonitoringOutOfBoundResult {

    private String dateAppointed;
    private Long productionUnitId;
    private Long opId;
    private String productionOrder;
    private String productCode;
    private Long refugoId;
    private double toleranceQuantity;
    private double lossQuantity; // quantidade refugada
    private String lossCode;
    private String lossName;
    private Long helpChainId;

}
