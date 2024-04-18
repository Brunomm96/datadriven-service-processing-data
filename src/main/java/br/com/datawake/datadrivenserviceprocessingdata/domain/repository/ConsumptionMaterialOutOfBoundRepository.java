package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionMaterialOutOfBound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumptionMaterialOutOfBoundRepository extends JpaRepository<ConsumptionMaterialOutOfBound, Long> {
}
