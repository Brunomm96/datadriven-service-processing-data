package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_consumption_material_by_product_out_of_bound")
public class ConsumptionMaterialByProductOutOfBound {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appointmentDate;
    private String productionOrder;
    private String productCode;
    private BigDecimal tolerancePercentMin;
    private BigDecimal tolerancePercentMax;
    private BigDecimal toleranceQuantity;
    private BigDecimal appointedAmount; // quantidade apontada
    private BigDecimal plannedQuantity; // quantidade planejada
    private BigDecimal amountRevenue; // quantidade receita
    private BigDecimal quantityPerPiece; // quantidade por peça
    private BigDecimal toleranceValueMin;
    private BigDecimal toleranceValueMax;
    private Long helpChainId;
    private Long etiquetaId;

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
