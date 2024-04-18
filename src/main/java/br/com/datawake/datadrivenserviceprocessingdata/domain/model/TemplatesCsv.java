package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_templates_csv")
public class TemplatesCsv{

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dwTable;

    private String description;

    private boolean staticTable;

    @ManyToOne()
    private TechnicalSheet technicalSheet;

    private String accessKey;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateInsert;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateUpdate;

    @PrePersist
    private void generateAccessKey() {
        if ( getDwTable() != null && !getDwTable().isEmpty() ){
            setStaticTable(true);
        }else{
            setStaticTable(false);
        }
        setAccessKey(DatadrivenUtils.getNewAccessKey());
    }

}
