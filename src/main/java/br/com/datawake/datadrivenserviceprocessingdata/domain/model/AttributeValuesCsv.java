package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeValuesCsv {

    private String valueString="''";
    private double valueNumber=0;
    private int valueBoolean=0;
    private String valueDate="'19000101'";
    private String valueList="''";
    private String valueMultiple="''";

}
