package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.RelationsOfForeignKey;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.TableStructModel;
import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
@Repository
public class StructCsvTemplateRepositoryImpl implements StructCsvTemplateRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DatadrivenUtils datadrivenUtils;

    @Autowired
    private RelationsOfForeignKeyRepository relationsOfForeignKeyRepository;

    private List<RelationsOfForeignKey> foreignKeysList;

    @Override
    public List<TableStructModel> list( String dwTable , String accessKey ) {

        if( dwTable != null && !dwTable.isEmpty() ){
            foreignKeysList = relationsOfForeignKeyRepository.findByDwTable( dwTable );
        }
        return loadColuns( dwTable , accessKey );

    }

    private List<TableStructModel> loadColuns(String dwTable , String accessKey){

        List<TableStructModel> columnsList = new ArrayList<TableStructModel>();
        StringBuilder sb = queryString( dwTable , accessKey );
        Query query = entityManager.createNativeQuery(sb.toString());
        if ( dwTable != null && !dwTable.isEmpty() ){
            query.setParameter("dwTable", dwTable);
        }
        if ( accessKey != null && !accessKey.isEmpty() ){
            query.setParameter("accessKey", accessKey);
        }

        List<Object[]> queryResp = query.getResultList();

        String[] columns = {
            "NameColumn"
            ,"TypeColumn"
            ,"LengthColumn"
            ,"DescriptionColumn"
            ,"ListOptions"
        };

        List<Map<String, String>> dataList = datadrivenUtils.queryToMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            columnsList.add(getTableStructModel(record));
        }

        return columnsList;

    }

    private StringBuilder queryString( String dwTable, String accessKey ){

        StringBuilder sb = new StringBuilder();

        if ( dwTable != null && !dwTable.isEmpty() ){
            sb.append(" SELECT  ");
            sb.append(" c.name AS NameColumn ");
            sb.append(" , t.name AS TypeColumn ");
            sb.append(" , (CASE WHEN c.max_length < 0 THEN 8000 ELSE c.max_length END ) AS LengthColumn ");
            sb.append(" , ''AS DescriptionColumn ");
            sb.append(" , ''AS ListOptions ");
            sb.append(" FROM sys.tables tab ");
            sb.append(" inner join sys.columns c ");
            sb.append(" on c.object_id = tab.object_id ");
            sb.append(" and c.name not in( ");
            sb.append(" 'id' ");
            sb.append(" ,'user_id_insert' ");
            sb.append(" ,'date_insert' ");
            sb.append(" ,'user_id_update' ");
            sb.append(" ,'date_update' ");
            sb.append(" ,'access_key' ");
            sb.append(" ) ");
            sb.append(" inner join sys.types t ");
            sb.append(" on t.system_type_id = c.system_type_id ");
            sb.append(" and t.user_type_id = c.user_type_id  ");
            sb.append(" WHERE  tab.name = :dwTable ");
            sb.append(" ORDER BY tab.name ");
        } else {
            sb.append(" SELECT a.attribute  AS NameColumn ");
            sb.append(" , a.type AS TypeColumn ");
            sb.append(" , a.size AS LengthColumn ");
            sb.append(" , a.description AS DescriptionColumn");
            sb.append(" , a.list_options As ListOptions");
            sb.append(" FROM dw_technical_sheet t ");
            sb.append(" inner join dw_technical_sheet_attributes s ");
            sb.append(" on s.technical_sheet_id = t.id ");
            sb.append(" inner join dw_attribute a ");
            sb.append(" on a.id = s.attribute_id ");
            sb.append(" where t.access_key = :accessKey ");
            sb.append(" order by s.order_field");
        }
        return sb;

    }

    private TableStructModel getTableStructModel( Map<String, String> line ){

        String column =  line.get("NameColumn").toString();
        Integer hasId = -1;
        if (foreignKeysList != null) {
            hasId = foreignKeysList.indexOf(foreignKeysList.stream().filter(
                x -> x.getDwForeignKey() != null && x.getDwForeignKey().equals( column )
                ).findFirst().orElse(null));
        }

        TableStructModel struct = new TableStructModel();
        struct.setNameColumn( column );
        struct.setTypeColumn(line.get("TypeColumn").toString().toUpperCase());
        struct.setLengthColumn(Integer.valueOf(line.get("LengthColumn").toString()));
        struct.setDescriptionColumn(line.get("DescriptionColumn").toString());

        if( struct.getTypeColumn().equals("DATE") ){
            struct.setDescriptionColumn( struct.getDescriptionColumn() + "(yyyymmdd)"  );
        }else if ( struct.getTypeColumn().equals("BOOLEAN") ){
            struct.setDescriptionColumn( struct.getDescriptionColumn() + "(0=false / 1=true)"  );
        }
        struct.setListOptions(line.get("ListOptions").toString());

        if (hasId >= 0 ){
            struct.setReferenceTable( foreignKeysList.get(hasId).getDwReferenceTable() );
            struct.setReferenceKeyTable( foreignKeysList.get(hasId).getDwReferenceKey());
        }

        return struct;

    }
}
