package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.exception.DomainException;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TechnicalSheet;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TechnicalSheetKey;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.TechnicalSheetKeyService;
import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TechnicalSheetCustomRepositoryImpl implements TechnicalSheetCustomRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DatadrivenUtils datadrivenUtils;

    @Autowired
    TechnicalSheetKeyService technicalSheetKeyService;
    private Optional<List<TechnicalSheetKey>> optionalTechnicalSheetKeyList;

    @Override
    public List<Map<String,String>> technicalSheetByKeys(TechnicalSheet technicalSheet , Map<String, Object> keyAttributes) {

        StringBuilder stringColumns = new StringBuilder();
        StringBuilder sb = getStringQuery(technicalSheet, stringColumns , keyAttributes );

        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("technicalSheetAccessKey", technicalSheet.getAccessKey());

        if (keyAttributes != null){
            keyAttributes.forEach( ( column , field )->{
                query.setParameter(column, field);
            });
        }

        List<Object[]> queryResp = query.getResultList();

        String[] columns = stringColumns.toString().split(",");

        List<Map<String, String>> dataList = datadrivenUtils.queryToMap(queryResp,columns);

        return dataList;

    }


    private StringBuilder getStringQuery(TechnicalSheet technicalSheet , StringBuilder stringColumns , Map<String, Object> keyAttributes) {

        optionalTechnicalSheetKeyList = technicalSheetKeyService.findByTechnicalSheet(technicalSheet);

        if ( optionalTechnicalSheetKeyList.isEmpty() )
            throw new DomainException("Não há atributos definidos como chave primária de busca de registros de ficha técnica!");

        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");

        sb.append(" recordAccessKey ");
        stringColumns.append("recordAccessKey");

        for (TechnicalSheetKey technicalSheetKey :
                optionalTechnicalSheetKeyList.get()) {

            String attribute = technicalSheetKey.getAttribute().getAttribute();

            sb.append("," + attribute);
            stringColumns.append("," + attribute);
        }

        sb.append(" FROM ( ");

        sb.append(" SELECT DISTINCT ");
        sb.append(" MIN(av.id) AS id");
        sb.append(" , av.record_access_key  AS recordAccessKey");

        for (TechnicalSheetKey technicalSheetKey :
                optionalTechnicalSheetKeyList.get()) {

            String attribute = technicalSheetKey.getAttribute().getAttribute();

            sb.append("," + attribute + ".content AS " + attribute);
        }

        sb.append(" FROM  ");
        sb.append(" dw_attribute_values av ");

        sb.append("INNER JOIN ");
        sb.append(" dw_technical_sheet ts ON ");
        sb.append(" ts.id = av.technical_sheet_id ");

        sb.append("INNER JOIN ");
        sb.append(" dw_attribute a ON ");
        sb.append(" a.id = av.attribute_id ");

        for (TechnicalSheetKey technicalSheetKey:
                optionalTechnicalSheetKeyList.get()) {

            String attribute = technicalSheetKey.getAttribute().getAttribute();

            sb.append("LEFT JOIN ");
            sb.append(" ( ");
            sb.append("  SELECT   ");

            sb.append("   av.record_access_key AS record ");

            sb.append("   , CASE ");
            sb.append("   WHEN a.type = 'STRING' THEN av.value_string ");
            sb.append("   WHEN a.type = 'NUMBER' THEN CONVERT(VARCHAR,av.value_number) ");
            sb.append("   WHEN a.type = 'LIST' THEN av.value_list ");
            sb.append("   WHEN a.type = 'DATE' THEN CONVERT(VARCHAR,av.value_date,112) ");
            sb.append("   WHEN a.type = 'BOOLEAN' THEN CONVERT(VARCHAR,av.value_boolean) ");
            sb.append("   ELSE 'UNDEFINED' ");
            sb.append("   END AS content ");

            sb.append("  FROM  ");
            sb.append("   dw_attribute_values av ");

            sb.append("  INNER JOIN ");
            sb.append("   dw_attribute a ON ");
            sb.append("   a.id = av.attribute_id ");

            sb.append("  WHERE ");
            sb.append("   a.attribute = '" + attribute + "' ");
            sb.append(" ) " + attribute + " ON ");
            sb.append( attribute + ".record = av.record_access_key ");

        }

        sb.append("WHERE ");
        sb.append("ts.access_key = :technicalSheetAccessKey ");

        if (keyAttributes != null){
            keyAttributes.forEach( ( column , field )->{
                sb.append(" and " + column + ".content    = :"+column + " ");
            });
        }

        sb.append("GROUP BY ");
        sb.append(" av.record_access_key ");

        for (TechnicalSheetKey technicalSheetKey :
                optionalTechnicalSheetKeyList.get()) {

            String attribute = technicalSheetKey.getAttribute().getAttribute();

            sb.append("," + attribute + ".content ");
        }

        sb.append(" ) TMP ");
        sb.append(" ORDER BY TMP.id ");

        return sb;
    }

}