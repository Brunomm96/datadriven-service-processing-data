package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_loss_monitoring_out_of_bound")
public class LossMonitoringOutOfBound {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appointmentDate;
    private Long productionOrderId;
    private String productCode;
    private Long productionUnitId;
    private Long refugoId;
    private double toleranceQuantity;
    private double lossQuantity; // quantidade refugada
    private Long helpChainId;

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
