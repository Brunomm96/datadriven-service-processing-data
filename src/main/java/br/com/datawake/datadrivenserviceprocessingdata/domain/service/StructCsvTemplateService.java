package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TableStructModel;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TemplatesCsv;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.StructCsvTemplateRepository;

@Service
public class StructCsvTemplateService {

    @Autowired
    private StructCsvTemplateRepository structCsvTemplateRepository;

    public List<TableStructModel> structCsv( TemplatesCsv templatesCsv ){

        var dwTable = "";
        var accessKeyTechnicalSheet = "";

        if ( templatesCsv.isStaticTable() ) {
            dwTable = templatesCsv.getDwTable();
        }else {
            accessKeyTechnicalSheet = templatesCsv.getTechnicalSheet().getAccessKey();
        }

        return list( dwTable , accessKeyTechnicalSheet );
    }

    public List<TableStructModel> list(String dwTable , String accessKey) {
        return structCsvTemplateRepository.list( dwTable , accessKey);
    }

}
