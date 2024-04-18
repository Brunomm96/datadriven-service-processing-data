package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableStructModel {

    private String nameColumn;
    private String typeColumn;
    private Integer lengthColumn;
    private String descriptionColumn;
    private String referenceTable;
    private String referenceKeyTable;
    private String listOptions;

}
