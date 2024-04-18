package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConsumptionOutOfBoundByProduct {

    private String date;
    private String op;
    private String productCode;
    private String productDescription;
    private BigDecimal tolerancePercentMin;
    private BigDecimal tolerancePercentMax;
    private BigDecimal toleranceQuantity;
    private BigDecimal plannedQuantity; // quantidade planejada
    private BigDecimal appointedAmount; // quantidade apontada
    private BigDecimal amountRevenue; // quantidade receita
    private BigDecimal quantityPerPiece; // quantidade por peça
    private BigDecimal toleranceMin;
    private BigDecimal toleranceMax;
    private Long helpChainId;
    private Long etiquetaId;

}
