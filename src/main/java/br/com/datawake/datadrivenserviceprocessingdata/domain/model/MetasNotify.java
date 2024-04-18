package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_metas_notify")
public class MetasNotify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int metaId;
    private OffsetDateTime dateNotify;

    @OneToOne
    private ConfigGoalProduct configGoalProduct;

    private Long helpChainId;
    private Long productionUnitId;
    private String productCode;
    private double productNewPieceHour;
    private double currentProductPieceHour;

    @Column(name = "access_key")
    private String accessKey;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateInsert;

    @PrePersist
    private void prePersist() {

        // Gerando access key
        generateAccessKey();

    }

    public void generateAccessKey() {
        setAccessKey(DatadrivenUtils.getNewAccessKey());
    }

}
