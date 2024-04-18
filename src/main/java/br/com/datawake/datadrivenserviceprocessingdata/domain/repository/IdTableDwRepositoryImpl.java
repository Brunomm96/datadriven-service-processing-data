package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.DwTableModel;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.FieldContentModel;
import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;

@Repository
public class IdTableDwRepositoryImpl implements IdTableDwRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DatadrivenUtils datadrivenUtils;

    @Override
    public String getIdTableDw( DwTableModel getIdTableModel ) {

        StringBuilder sb = getStringQuery( getIdTableModel );
        String id="";

        Query query = entityManager.createNativeQuery(sb.toString());

        List<Object[]> queryResp = query.getResultList();

        String[] columns = {
            "id"
        };

        List<Map<String, String>> dataList = datadrivenUtils.queryToMap(queryResp,columns);

        for ( Map<String,String> record : dataList ) {
            id = record.get("id").toString();
        }
        return id;

    }

    private StringBuilder getStringQuery( DwTableModel getIdTableModel  ) {

        StringBuilder sb = new StringBuilder();
        int i = 0 ;

        List<FieldContentModel> wheres =  getIdTableModel.getWhereDw();

        sb.append(" SELECT TOP 1 id , access_key  ");
        sb.append(" FROM " +  getIdTableModel.getTableDw() );

        for ( FieldContentModel where : wheres ){
            i++;
            if (i == 1){
                sb.append(" where ");
            }else {
                sb.append(" and ");
            }

            sb.append( where.getField() +" = '" + where.getContent() +"' ");

        }

        return sb;
    }

}
