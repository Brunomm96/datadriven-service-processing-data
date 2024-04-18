package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.MetasNotifyModel;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MetasCustomRepositoryImpl implements MetasCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void execProcMetas( String db, String productionUnit ) {

        StringBuilder sb = new StringBuilder();
        sb.append("EXEC ");
        sb.append("[");
        sb.append(db);
        sb.append("].[dbo].[P_DW_METAS_PROC] ");
        sb.append("@unidade_producao = :productionUnit ");

        entityManager.createNativeQuery(sb.toString())
                .setParameter("productionUnit", productionUnit)
                .executeUpdate();

    }

    @Override
    public List<MetasNotifyModel> execMetasNotify(String db, String productionUnit) {

        List<MetasNotifyModel> newMetas = new ArrayList<MetasNotifyModel>();

        StringBuilder sb = new StringBuilder();
        sb.append("EXEC ");
        sb.append("[");
        sb.append(db);
        sb.append("].[dbo].[P_DW_METAS_NOTIFY] ");
        sb.append("@unidade_filial = :productionUnit ");

        List<Object[]> queryResp = entityManager.createNativeQuery(sb.toString())
                                        .setParameter("productionUnit", productionUnit).getResultList();

        String[] columns = {
                "meta_id"
                ,"config_goal_product_id"
                ,"help_chain_id"
                ,"production_unit_id"
                ,"product_code"
                ,"product_name"
                ,"product_new_piece_hour"
                ,"current_product_piece_hour"
                ,"production_unit_code"
                ,"production_unit_name"
        };

        List<Map<String, String>> dataList = toMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            newMetas.add(getMetasNotifyResult(record));
        }

        return newMetas;
    }

    private MetasNotifyModel getMetasNotifyResult(Map<String, String> record) {

        MetasNotifyModel metasNotifyResult = new MetasNotifyModel();

        if ( record.get("ErrorMessage") == null ) {
            metasNotifyResult.setMetaId(Integer.valueOf(record.get("meta_id")));
            metasNotifyResult.setConfigGoalProductId(Long.valueOf(record.get("config_goal_product_id")));
            metasNotifyResult.setHelpChainId(Long.valueOf( ( record.get("help_chain_id") == "" ) ? "0" : record.get("help_chain_id") ));
            metasNotifyResult.setProductionUnitId(Long.valueOf(record.get("production_unit_id")));
            metasNotifyResult.setProductCode(record.get("product_code"));
            metasNotifyResult.setProductName(record.get("product_name"));
            metasNotifyResult.setProductNewPieceHour(Double.valueOf(record.get("product_new_piece_hour")));
            metasNotifyResult.setCurrentProductPieceHour(Double.valueOf(record.get("current_product_piece_hour")));
            metasNotifyResult.setProductionUnitCode(record.get("production_unit_code"));
            metasNotifyResult.setProductionUnitName(record.get("production_unit_name"));
        }

        return metasNotifyResult;

    }

    private List<Map<String, String>> toMap(List<Object[]> queryResp, String[] columns) {
        List<Map<String,String>> dataList = new ArrayList<>();

        for(Object[] obj : queryResp) {
            Map<String,String> row = new HashMap<>(columns.length);
            for(int i = 0; i< columns.length; i++) {
                if(obj[i]!=null)
                    row.put(columns[i], obj[i].toString());
                else
                    row.put(columns[i], "");
            }
            dataList.add(row);
        }
        return dataList;
    }

}
