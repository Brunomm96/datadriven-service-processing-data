package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionMaterialByProductOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionMaterialOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBoundByProduct;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.ConsumptionMaterialByProductOutOfBoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ConsumptionMaterialByProductOutOfBoundService {

    @Autowired
    ConsumptionMaterialByProductOutOfBoundRepository consumptionMaterialByProductOutOfBoundRepository;

    @Transactional
    public ConsumptionMaterialByProductOutOfBound save( ConsumptionMaterialByProductOutOfBound consumptionMaterialByProductOutOfBound) {
        return consumptionMaterialByProductOutOfBoundRepository.save(consumptionMaterialByProductOutOfBound);
    }

    public ConsumptionMaterialByProductOutOfBound saveByConsumptionOutOfBoundByProduct(ConsumptionOutOfBoundByProduct consumptionMonitoringByProductResult) {
        return save( toConsumptionMaterialByProductOutOfBound(consumptionMonitoringByProductResult) );
    }

    private ConsumptionMaterialByProductOutOfBound toConsumptionMaterialByProductOutOfBound(ConsumptionOutOfBoundByProduct consumptionMonitoringByProductResult) {

        ConsumptionMaterialByProductOutOfBound consumptionMaterialByProductOutOfBound = new ConsumptionMaterialByProductOutOfBound();
        consumptionMaterialByProductOutOfBound.setAppointmentDate(consumptionMonitoringByProductResult.getDate());
        consumptionMaterialByProductOutOfBound.setAmountRevenue( consumptionMonitoringByProductResult.getAmountRevenue() );
        consumptionMaterialByProductOutOfBound.setAppointedAmount( consumptionMonitoringByProductResult.getAppointedAmount() );
        consumptionMaterialByProductOutOfBound.setHelpChainId( consumptionMonitoringByProductResult.getHelpChainId() );
        consumptionMaterialByProductOutOfBound.setProductCode( consumptionMonitoringByProductResult.getProductCode() );
        consumptionMaterialByProductOutOfBound.setPlannedQuantity( consumptionMonitoringByProductResult.getPlannedQuantity() );
        consumptionMaterialByProductOutOfBound.setProductionOrder( consumptionMonitoringByProductResult.getOp() );
        consumptionMaterialByProductOutOfBound.setQuantityPerPiece( consumptionMonitoringByProductResult.getQuantityPerPiece() );
        consumptionMaterialByProductOutOfBound.setTolerancePercentMax( consumptionMonitoringByProductResult.getTolerancePercentMax());
        consumptionMaterialByProductOutOfBound.setTolerancePercentMin( consumptionMonitoringByProductResult.getTolerancePercentMin());
        consumptionMaterialByProductOutOfBound.setToleranceValueMax( consumptionMonitoringByProductResult.getToleranceMax());
        consumptionMaterialByProductOutOfBound.setToleranceValueMin( consumptionMonitoringByProductResult.getToleranceMin());
        consumptionMaterialByProductOutOfBound.setToleranceQuantity( consumptionMonitoringByProductResult.getToleranceQuantity());
        consumptionMaterialByProductOutOfBound.setEtiquetaId(consumptionMonitoringByProductResult.getEtiquetaId());

        return consumptionMaterialByProductOutOfBound;

    }
}
