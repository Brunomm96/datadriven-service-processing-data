package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Getter
@Setter
public class MetasNotifyModel {

    private int metaId;
    private Long configGoalProductId;
    private Long helpChainId;
    private Long productionUnitId;
    private String productCode;
    private String productName;
    private double productNewPieceHour;
    private double currentProductPieceHour;
    private String productionUnitCode;
    private String productionUnitName;

}
