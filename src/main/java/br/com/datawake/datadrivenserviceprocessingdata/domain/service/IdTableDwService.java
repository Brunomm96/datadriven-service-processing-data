package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.DwTableModel;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.IdTableDwRepository;

@Service
public class IdTableDwService {

    @Autowired
    private IdTableDwRepository getIdTableDwRepository;

    public String getIdTableDw( DwTableModel getIdTableModel ){
        return getIdTableDwRepository.getIdTableDw( getIdTableModel);
    }

}
