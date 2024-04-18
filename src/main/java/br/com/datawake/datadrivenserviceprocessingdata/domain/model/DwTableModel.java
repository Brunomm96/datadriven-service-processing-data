package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DwTableModel {

    private String tableDw;
    private List<FieldContentModel> whereDw ;

    public DwTableModel(){}

    public DwTableModel(String tableDw) {
        this.tableDw = tableDw;
    }

    public DwTableModel(String tableDw, List<FieldContentModel> whereDw) {
        this.tableDw = tableDw;
        this.whereDw = whereDw;
    }

}
