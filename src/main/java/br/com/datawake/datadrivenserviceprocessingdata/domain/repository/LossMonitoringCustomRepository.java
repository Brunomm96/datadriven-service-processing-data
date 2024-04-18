package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.LossMonitoringOutOfBoundResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LossMonitoringCustomRepository {

    List<LossMonitoringOutOfBoundResult> listLossMonitoringOutOfBound(String db, String productionUnit);

    List<LossMonitoringOutOfBoundResult> listLossMonitoringOutOfBoundByProduct(String db, String productionUnit);
}
