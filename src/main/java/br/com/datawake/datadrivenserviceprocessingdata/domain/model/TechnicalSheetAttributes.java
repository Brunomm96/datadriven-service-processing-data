package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity(name = "dw_technical_sheet_attributes")
public class TechnicalSheetAttributes {

    @EmbeddedId
    private TechnicalSheetAttributesPK technicalSheetAttributesPK;

    @MapsId("technicalSheetId")
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="technical_sheet_id", referencedColumnName="id")
    private TechnicalSheet technicalSheet;


    @MapsId("attributeId")
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="attribute_id", referencedColumnName="id")
    private Attribute attribute;

    private int orderField;

}
