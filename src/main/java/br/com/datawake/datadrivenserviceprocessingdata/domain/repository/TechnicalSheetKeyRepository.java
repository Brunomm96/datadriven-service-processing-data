package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TechnicalSheet;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TechnicalSheetKey;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicalSheetKeyRepository extends JpaRepository<TechnicalSheetKey, Long> {

    Optional<TechnicalSheetKey> findByAccessKey(String accessKey);

    Optional<TechnicalSheetKey> findByTechnicalSheetAccessKeyAndAttributeAccessKey(String technicalSheetAccessKey, String attributeAccessKey);

    Optional<List<TechnicalSheetKey>> findByTechnicalSheet(TechnicalSheet technicalSheet);
}
