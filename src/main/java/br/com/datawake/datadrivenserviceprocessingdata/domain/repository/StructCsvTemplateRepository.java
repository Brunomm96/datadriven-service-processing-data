package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TableStructModel;

@Repository
public interface StructCsvTemplateRepository {
    public List<TableStructModel> list( String dwTable , String accessKey );
}
