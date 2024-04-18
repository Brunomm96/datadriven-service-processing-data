package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TechnicalSheet;

import java.util.List;
import java.util.Map;

@Repository
public interface TechnicalSheetCustomRepository {

    List<Map<String, String>> technicalSheetByKeys(TechnicalSheet technicalSheet , Map<String, Object> keyAttributes );

}
