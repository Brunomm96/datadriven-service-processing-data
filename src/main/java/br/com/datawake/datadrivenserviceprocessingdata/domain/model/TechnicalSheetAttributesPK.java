package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
public class TechnicalSheetAttributesPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    @Column(name = "technical_sheet_id")
    private long technicalSheetId;

    @EqualsAndHashCode.Include
    @Column(name = "attribute_id")
    private long attributeId;
}