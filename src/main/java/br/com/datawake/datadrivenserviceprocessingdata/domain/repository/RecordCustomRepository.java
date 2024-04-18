package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface RecordCustomRepository {

    void execProcRecord(String db, String productionUnit);

}
