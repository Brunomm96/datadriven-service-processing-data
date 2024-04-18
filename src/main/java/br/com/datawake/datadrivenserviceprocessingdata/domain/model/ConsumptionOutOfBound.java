package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumptionOutOfBound {

    private int consumoMaterialApontamentoId;
    private String dateHourAppointed;
    private String op;
    private String productCode;
    private String productDescription;
    private String componentCode;
    private double tolerancePercentMin;
    private double tolerancePercentMax;
    private double toleranceQuantity;
    private double appointedAmount; // quantidade apontada
    private double plannedQuantity; // quantidade planejada
    private double amountRevenue; // quantidade receita
    private double quantityPerPiece; // quantidade por peça
    private double toleranceMin;
    private double toleranceMax;
    private int pieceQuantity; //Quantidade peça
    private Long helpChainId;

}
