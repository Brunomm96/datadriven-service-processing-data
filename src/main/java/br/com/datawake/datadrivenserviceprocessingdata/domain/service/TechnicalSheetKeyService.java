package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TechnicalSheet;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TechnicalSheetKey;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.TechnicalSheetKeyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TechnicalSheetKeyService {

    @Autowired
    private TechnicalSheetKeyRepository technicalSheetKeyRepository;

    public Optional<List<TechnicalSheetKey>> findByTechnicalSheet(TechnicalSheet technicalSheet) {
        return technicalSheetKeyRepository.findByTechnicalSheet(technicalSheet);
    }

}
