package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.UploadsCsvTemplateLog;

@Repository
public interface UploadsCsvTemplateLogRepository extends JpaRepository<UploadsCsvTemplateLog, Long> {

}
