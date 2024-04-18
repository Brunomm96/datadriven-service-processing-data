package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_consumption_material_out_of_bound")
public class ConsumptionMaterialOutOfBound {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime appointmentDate;
    private String productionOrder;
    private String productCode;
    private String componentCode;
    private double tolerancePercentMin;
    private double tolerancePercentMax;
    private double toleranceQuantity;
    private double appointedAmount; // quantidade apontada
    private double plannedQuantity; // quantidade planejada
    private double amountRevenue; // quantidade receita
    private double quantityPerPiece; // quantidade por peça
    private double toleranceValueMin;
    private double toleranceValueMax;
    private Long helpChainId;
    private int consumoMaterialApontamentoId;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateInsert;

    @Column(name = "access_key")
    private String accessKey;

    @PrePersist
    private void generateAccessKey() {
        setAccessKey(DatadrivenUtils.getNewAccessKey());
    }

}
