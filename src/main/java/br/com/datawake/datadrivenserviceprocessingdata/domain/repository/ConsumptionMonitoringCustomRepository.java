package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBoundByProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumptionMonitoringCustomRepository {

    List<ConsumptionOutOfBound> listConsumptionsOutOfBound(String db, String productionUnit);

    List<ConsumptionOutOfBoundByProduct> listConsumptionsOutOfBoundByProduct(String db, String productionUnit);
}
