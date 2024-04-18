package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_technical_sheet")
public class TechnicalSheet {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String description;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="technicalSheet")
    private Set<TechnicalSheetAttributes> attributes = new HashSet<>();

    @Column(name = "access_key")
    private String accessKey;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateInsert;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateUpdate;

    public boolean removeAttribute(Attribute attribute) {
        return getAttributes().remove(attribute);
    }

    public boolean addAttribute(TechnicalSheetAttributes attribute) {
        return getAttributes().add(attribute);
    }

    @PrePersist
    private void prePersist() {
        // Gerando access key
        generateAccessKey();

    }

    public void generateAccessKey() {
        setAccessKey(DatadrivenUtils.getNewAccessKey());
    }

}
