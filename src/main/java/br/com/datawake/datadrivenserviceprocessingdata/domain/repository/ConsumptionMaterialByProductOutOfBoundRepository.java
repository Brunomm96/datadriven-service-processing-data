package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionMaterialByProductOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionMaterialOutOfBound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumptionMaterialByProductOutOfBoundRepository extends JpaRepository<ConsumptionMaterialByProductOutOfBound, Long> {
}
