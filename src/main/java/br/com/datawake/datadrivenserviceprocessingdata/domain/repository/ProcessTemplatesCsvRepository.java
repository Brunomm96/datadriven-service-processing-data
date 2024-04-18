package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface ProcessTemplatesCsvRepository {

    void execTemplatesPending( String db );

}
