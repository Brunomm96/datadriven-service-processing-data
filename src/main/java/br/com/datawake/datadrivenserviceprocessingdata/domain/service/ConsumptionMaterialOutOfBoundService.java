package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionMaterialOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.ConsumptionMaterialOutOfBoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Service
public class ConsumptionMaterialOutOfBoundService {

    @Autowired
    ConsumptionMaterialOutOfBoundRepository consumptionMaterialOutOfBoundRepository;

    @Transactional
    public ConsumptionMaterialOutOfBound save( ConsumptionMaterialOutOfBound consumptionMaterialOutOfBound) {
        return consumptionMaterialOutOfBoundRepository.save(consumptionMaterialOutOfBound);
    }

    public ConsumptionMaterialOutOfBound saveByConsumptionOutOfBound( ConsumptionOutOfBound consumptionOutOfBound) {
        return save( toConsumptionMaterialOutOfBound(consumptionOutOfBound) );
    }

    private ConsumptionMaterialOutOfBound toConsumptionMaterialOutOfBound(ConsumptionOutOfBound consumptionOutOfBound) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern ("dd/MM/yyyy - HH:mm:ss");
        OffsetDateTime dateAppointed = LocalDateTime.parse(consumptionOutOfBound.getDateHourAppointed(), dtf)
                .atZone(TimeZone.getTimeZone("UTC").toZoneId())
                .toOffsetDateTime();

        ConsumptionMaterialOutOfBound consumptionMaterialOutOfBound = new ConsumptionMaterialOutOfBound();
        consumptionMaterialOutOfBound.setAppointmentDate(dateAppointed);
        consumptionMaterialOutOfBound.setAmountRevenue( consumptionOutOfBound.getAmountRevenue() );
        consumptionMaterialOutOfBound.setAppointedAmount( consumptionOutOfBound.getAppointedAmount() );
        consumptionMaterialOutOfBound.setComponentCode( consumptionOutOfBound.getComponentCode() );
        consumptionMaterialOutOfBound.setHelpChainId( consumptionOutOfBound.getHelpChainId() );
        consumptionMaterialOutOfBound.setProductCode( consumptionOutOfBound.getProductCode() );
        consumptionMaterialOutOfBound.setPlannedQuantity( consumptionOutOfBound.getPlannedQuantity() );
        consumptionMaterialOutOfBound.setProductionOrder( consumptionOutOfBound.getOp() );
        consumptionMaterialOutOfBound.setQuantityPerPiece( consumptionOutOfBound.getQuantityPerPiece() );
        consumptionMaterialOutOfBound.setTolerancePercentMax( consumptionOutOfBound.getTolerancePercentMax());
        consumptionMaterialOutOfBound.setTolerancePercentMin( consumptionOutOfBound.getTolerancePercentMin());
        consumptionMaterialOutOfBound.setToleranceValueMax( consumptionOutOfBound.getToleranceMax());
        consumptionMaterialOutOfBound.setToleranceValueMin( consumptionOutOfBound.getToleranceMin());
        consumptionMaterialOutOfBound.setToleranceQuantity( consumptionOutOfBound.getToleranceQuantity());
        consumptionMaterialOutOfBound.setConsumoMaterialApontamentoId( consumptionOutOfBound.getConsumoMaterialApontamentoId() );

        return consumptionMaterialOutOfBound;

    }


}
