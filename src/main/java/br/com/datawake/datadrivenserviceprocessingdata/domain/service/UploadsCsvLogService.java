package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.UploadsCsvTemplateLog;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.UploadsCsvTemplateLogRepository;


@Service
public class UploadsCsvLogService {

    @Autowired
    private UploadsCsvTemplateLogRepository uploadsCsvTemplateLogRepository;

    public Page<UploadsCsvTemplateLog> findAll(Pageable pageable) {
        return uploadsCsvTemplateLogRepository.findAll(pageable);
    }

    @Transactional
    public UploadsCsvTemplateLog save(UploadsCsvTemplateLog uploadCsvLog ) {
        return uploadsCsvTemplateLogRepository.save(uploadCsvLog);
    }

}
