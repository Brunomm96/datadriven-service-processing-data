package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.UploadCsvStatus;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.UploadsCsvTemplate;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.UploadsCsvTemplateRepository;

@Service
public class UploadsCsvService {
    @Autowired
    private UploadsCsvTemplateRepository uploadsCsvRepository;

    public List<UploadsCsvTemplate>findByStatusFile( UploadCsvStatus status  ) {
        return uploadsCsvRepository.findByStatusFile( status);
    }

    public UploadsCsvTemplate changeStatusFile(UploadsCsvTemplate uploadCsv, UploadCsvStatus status ) {
        uploadCsv.setStatusFile( status );
        return save(uploadCsv);
    }

    @Transactional
    public UploadsCsvTemplate save(UploadsCsvTemplate uploadCsv) {
        return uploadsCsvRepository.save(uploadCsv);
    }

}
