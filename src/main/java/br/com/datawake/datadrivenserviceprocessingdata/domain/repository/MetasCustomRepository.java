package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.MetasNotify;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.MetasNotifyModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetasCustomRepository {

    void execProcMetas(String db, String productionUnit);

    List<MetasNotifyModel> execMetasNotify(String db, String productionUnit);
}
