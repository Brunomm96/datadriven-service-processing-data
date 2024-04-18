package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.LossMonitoringOutOfBoundResult;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LossMonitoringCustomRepositoryImpl implements LossMonitoringCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<LossMonitoringOutOfBoundResult> listLossMonitoringOutOfBound(String db, String productionUnit ) {

        List<LossMonitoringOutOfBoundResult> lossMonitoringsOutOfBound = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("EXEC ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].[P_DW_MONITORAMENTO_REFUGO] ");
        sb.append("@unidade_filial = :filial ");
        sb.append(", @DTMONITORA = NULL ");

        List<Object[]> queryResp = entityManager.createNativeQuery(sb.toString())
                .setParameter("filial",productionUnit)
                .getResultList();

        String[] columns = {
                "unidade_producao_id"
                ,"ordem_producao_id"
                ,"nr_ordem_producao"
                ,"refugo_id"
                ,"data"
                ,"product_code"
                ,"tolerance_quantity"
                ,"quantidade_refugada"
                ,"refugo_codigo"
                ,"refugo_nome"
                ,"help_chain_id"
        };

        List<Map<String, String>> dataList = toMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            lossMonitoringsOutOfBound.add(getLossMonitoringResult(record));
        }

        return lossMonitoringsOutOfBound;

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

    private LossMonitoringOutOfBoundResult getLossMonitoringResult(Map<String, String> record) {

        LossMonitoringOutOfBoundResult lossMonitoringOutOfBoundResult = new LossMonitoringOutOfBoundResult();

        if ( record.get("ErrorMessage") == null ) {
            lossMonitoringOutOfBoundResult.setProductionUnitId(Long.valueOf(record.get("unidade_producao_id")));
            lossMonitoringOutOfBoundResult.setProductionUnitId(Long.valueOf(record.get("ordem_producao_id")));
            lossMonitoringOutOfBoundResult.setProductionOrder(record.get("nr_ordem_producao"));
            lossMonitoringOutOfBoundResult.setProductionUnitId(Long.valueOf(record.get("refugo_id")));
            lossMonitoringOutOfBoundResult.setDateAppointed(record.get("data"));
            lossMonitoringOutOfBoundResult.setProductCode(record.get("product_code"));
            lossMonitoringOutOfBoundResult.setToleranceQuantity(Double.valueOf(record.get("tolerance_quantity")));
            lossMonitoringOutOfBoundResult.setLossQuantity(Double.valueOf(record.get("quantidade_refugada")));
            lossMonitoringOutOfBoundResult.setLossCode(record.get("refugo_codigo"));
            lossMonitoringOutOfBoundResult.setLossName(record.get("refugo_nome"));
            lossMonitoringOutOfBoundResult.setHelpChainId(Long.valueOf( ( record.get("help_chain_id") == "" ) ? "0" : record.get("help_chain_id") ));
        }

        return lossMonitoringOutOfBoundResult;
    }

    @Override
    public List<LossMonitoringOutOfBoundResult> listLossMonitoringOutOfBoundByProduct(String db, String productionUnit ) {

        List<LossMonitoringOutOfBoundResult> lossMonitoringnsOutOfBoundByProduct = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("EXEC ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].[P_DW_MONITORAMENTO_REFUGO_POR_PECA] ");
        sb.append("@unidade_filial = :filial ");
        sb.append(", @DTMONITORA = NULL ");

        List<Object[]> queryResp = entityManager.createNativeQuery(sb.toString())
                .setParameter("filial",productionUnit)
                .getResultList();

        String[] columns = {
                "unidade_producao_id"
                ,"ordem_producao_id"
                ,"nr_ordem_producao"
                ,"data"
                ,"product_code"
                ,"tolerance_quantity"
                ,"quantidade_refugada"
                ,"help_chain_id"
        };

        List<Map<String, String>> dataList = toMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            lossMonitoringnsOutOfBoundByProduct.add(getLossMonitoringByProductResult(record));
        }

        return lossMonitoringnsOutOfBoundByProduct;

    }

    private LossMonitoringOutOfBoundResult getLossMonitoringByProductResult(Map<String, String> record) {

        LossMonitoringOutOfBoundResult lossMonitoringOutOfBoundResult = new LossMonitoringOutOfBoundResult();

        if ( record.get("ErrorMessage") == null ) {
            lossMonitoringOutOfBoundResult.setProductionUnitId(Long.valueOf(record.get("unidade_producao_id")));
            lossMonitoringOutOfBoundResult.setOpId(Long.valueOf(record.get("ordem_producao_id")));
            lossMonitoringOutOfBoundResult.setProductionOrder(record.get("nr_ordem_producao"));
            lossMonitoringOutOfBoundResult.setDateAppointed(record.get("data"));
            lossMonitoringOutOfBoundResult.setProductCode(record.get("product_code"));
            lossMonitoringOutOfBoundResult.setToleranceQuantity(Double.valueOf(record.get("tolerance_quantity")));
            lossMonitoringOutOfBoundResult.setLossQuantity(Double.valueOf(record.get("quantidade_refugada")));
            lossMonitoringOutOfBoundResult.setHelpChainId(Long.valueOf( ( record.get("help_chain_id") == "" ) ? "0" : record.get("help_chain_id") ));
        }

        return lossMonitoringOutOfBoundResult;

    }
}
