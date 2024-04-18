package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.LossMonitoringOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.LossMonitoringOutOfBoundResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LossMonitoringOutOfBoundRepository extends JpaRepository<LossMonitoringOutOfBound, Long> {
}
