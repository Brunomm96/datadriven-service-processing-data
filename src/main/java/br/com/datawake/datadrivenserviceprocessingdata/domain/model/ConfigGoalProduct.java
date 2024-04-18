package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "dw_config_goal_product")
public class ConfigGoalProduct  {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productCode;

    private double goalDefault;
    private double goalCalculated;
    private double qtyDeliveryToCalc;
    private int scheduleFrequency;
    private double percentOutlier;

    @Column(name = "access_key")
    private String accessKey;

}
