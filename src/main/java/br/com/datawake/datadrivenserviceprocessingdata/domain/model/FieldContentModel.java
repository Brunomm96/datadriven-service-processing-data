package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldContentModel {
    private String field;
    private String content;

    public FieldContentModel(String field, String content) {
        this.field = field;
        this.content = content;
    }

}
