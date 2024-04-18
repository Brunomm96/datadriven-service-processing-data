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
@Entity(name = "dw_attribute")
public class Attribute  {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String attribute;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private boolean active;

    private boolean required;

    @Enumerated(EnumType.STRING)
    private AttributeTypes type;

    private Integer size;
    private Integer precision;

    @Column(name = "list_options")
    private String listOptions;

    private boolean editable;

    private int microtimeId;

    @ManyToOne
    private AttributeGroup attributeGroup;

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
