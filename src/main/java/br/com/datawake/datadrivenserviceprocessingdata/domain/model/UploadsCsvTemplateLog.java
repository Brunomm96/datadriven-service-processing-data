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
@Entity(name = "dw_uploads_csv_template_log")
public class UploadsCsvTemplateLog {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uploads_csv_template_id", nullable = false)
    private UploadsCsvTemplate uploadsCsvTemplate;

    private Long lineFile;

    private String contentLine;

    private String messageLine;

    @Column(name = "access_key")
    private String accessKey;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateInsert;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateUpdate;

    @PrePersist
    private void generateAccessKey() {
        setAccessKey(DatadrivenUtils.getNewAccessKey());
    }

}
