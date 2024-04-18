package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.DwTableModel;


@Repository
public interface IdTableDwRepository{
    public String getIdTableDw( DwTableModel getIdTableModel );
}
