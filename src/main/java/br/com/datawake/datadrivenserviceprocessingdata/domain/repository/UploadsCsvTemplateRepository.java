package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.UploadCsvStatus;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.UploadsCsvTemplate;

@Repository
public interface UploadsCsvTemplateRepository extends JpaRepository<UploadsCsvTemplate, Long> {

    List<UploadsCsvTemplate> findByStatusFile( UploadCsvStatus uploadCsvStatus );

}
