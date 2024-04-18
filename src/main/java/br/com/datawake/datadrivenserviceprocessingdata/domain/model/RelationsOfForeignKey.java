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
@Entity(name = "dw_relations_of_foreign_key")
public class RelationsOfForeignKey {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dwTable;

    private String dwTableKey;

    private String dwForeignKey;

    private String dwReferenceTable;

    private String dwReferenceKey;

    private String accessKey;

    /*
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_insert")
    private User userInsert;
    */

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateInsert;

    /*
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_update")
    private User userUpdate;
    */

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "datetime")
    private OffsetDateTime dateUpdate;

    @PrePersist
    private void generateAccessKey() {
        setAccessKey(DatadrivenUtils.getNewAccessKey());
    }

}
