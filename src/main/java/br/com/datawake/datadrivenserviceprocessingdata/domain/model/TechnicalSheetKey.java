package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_technical_sheet_key")
public class TechnicalSheetKey {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TechnicalSheet technicalSheet;

    @ManyToOne
    private Attribute attribute;

    @Column(name = "access_key")
    private String accessKey;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateInsert;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateUpdate;

    @PrePersist
    private void prePersist() {
        // Gerando access key
        generateAccessKey();

    }

    public void generateAccessKey() {
        setAccessKey(DatadrivenUtils.getNewAccessKey());
    }

}
