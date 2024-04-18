package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBoundByProduct;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ConsumptionMonitoringCustomRepositoryImpl implements ConsumptionMonitoringCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ConsumptionOutOfBound> listConsumptionsOutOfBound( String db, String productionUnit ) {

        List<ConsumptionOutOfBound> consumptionsOutOfBound = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("EXEC ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].[P_DW_MONITORAMENTO_CONSUMO] ");
        sb.append("@unidade_filial = :filial ");
        sb.append(", @DTMONITORA = NULL ");

        List<Object[]> queryResp = entityManager.createNativeQuery(sb.toString())
                .setParameter("filial",productionUnit)
                .getResultList();

        String[] columns = {
                "id"
                ,"data_hora_apontamento"
                ,"nr_ordem_producao"
                ,"product_code"
                ,"product_name"
                ,"component_code"
                ,"tolerance_percent_min"
                ,"tolerance_percent_max"
                ,"tolerance_quantity"
                ,"quantidade_apontada"
                ,"quantidade_planejada"
                ,"quantidade_receita"
                ,"qtdporpeca"
                ,"help_chain_id"
                ,"tolerance_min"
                ,"tolerance_max"
        };

        List<Map<String, String>> dataList = toMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            consumptionsOutOfBound.add(getConsumptionMonitoringResult(record));
        }

        return consumptionsOutOfBound;

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

    private ConsumptionOutOfBound getConsumptionMonitoringResult(Map<String, String> record) {

        ConsumptionOutOfBound consumptionMonitoringResult = new ConsumptionOutOfBound();

        if ( record.get("ErrorMessage") == null ) {
            consumptionMonitoringResult.setConsumoMaterialApontamentoId(Integer.valueOf(record.get("id")));
            consumptionMonitoringResult.setDateHourAppointed(record.get("data_hora_apontamento"));
            consumptionMonitoringResult.setOp(record.get("nr_ordem_producao"));
            consumptionMonitoringResult.setProductCode(record.get("product_code"));
            consumptionMonitoringResult.setProductDescription(record.get("product_name"));
            consumptionMonitoringResult.setComponentCode(record.get("component_code"));
            consumptionMonitoringResult.setTolerancePercentMin(Double.valueOf(record.get("tolerance_percent_min")));
            consumptionMonitoringResult.setTolerancePercentMax(Double.valueOf(record.get("tolerance_percent_max")));
            consumptionMonitoringResult.setToleranceQuantity(Double.valueOf(record.get("tolerance_quantity")));
            consumptionMonitoringResult.setAppointedAmount(Double.valueOf(record.get("quantidade_apontada")));
            consumptionMonitoringResult.setPlannedQuantity(Double.valueOf(record.get("quantidade_planejada")));
            consumptionMonitoringResult.setAmountRevenue(Double.valueOf(record.get("quantidade_receita")));
            consumptionMonitoringResult.setQuantityPerPiece(Double.valueOf(record.get("qtdporpeca")));
            consumptionMonitoringResult.setHelpChainId(Long.valueOf( ( record.get("help_chain_id") == "" ) ? "0" : record.get("help_chain_id") ));
            consumptionMonitoringResult.setToleranceMin(Double.valueOf(record.get("tolerance_min")));
            consumptionMonitoringResult.setToleranceMax(Double.valueOf(record.get("tolerance_max")));
        }

        return consumptionMonitoringResult;
    }

    @Override
    public List<ConsumptionOutOfBoundByProduct> listConsumptionsOutOfBoundByProduct(String db, String productionUnit ) {

        List<ConsumptionOutOfBoundByProduct> consumptionsOutOfBoundByProduct = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("EXEC ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].[P_DW_MONITORAMENTO_CONSUMO_POR_PECA] ");
        sb.append("@unidade_filial = :filial ");
        sb.append(", @DTMONITORA = NULL ");

        List<Object[]> queryResp = entityManager.createNativeQuery(sb.toString())
                .setParameter("filial",productionUnit)
                .getResultList();

        String[] columns = {
                "data"
                ,"nr_ordem_producao"
                ,"product_code"
                ,"product_name"
                ,"tolerance_percent_min"
                ,"tolerance_percent_max"
                ,"tolerance_quantity"
                ,"quantidade_planejada"
                ,"quantidade_apontada"
                ,"quantidade_receita"
                ,"qtdporpeca"
                ,"help_chain_id"
                ,"etiqueta_id"
                ,"tolerance_min"
                ,"tolerance_max"
        };

        List<Map<String, String>> dataList = toMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            consumptionsOutOfBoundByProduct.add(getConsumptionMonitoringByProductResult(record));
        }

        return consumptionsOutOfBoundByProduct;

    }

    private ConsumptionOutOfBoundByProduct getConsumptionMonitoringByProductResult(Map<String, String> record) {

        ConsumptionOutOfBoundByProduct consumptionMonitoringByProductResult = new ConsumptionOutOfBoundByProduct();

        if ( record.get("ErrorMessage") == null ) {
            consumptionMonitoringByProductResult.setDate(record.get("data"));
            consumptionMonitoringByProductResult.setOp(record.get("nr_ordem_producao"));
            consumptionMonitoringByProductResult.setProductCode(record.get("product_code"));
            consumptionMonitoringByProductResult.setProductDescription(record.get("product_name"));
            consumptionMonitoringByProductResult.setTolerancePercentMin(new BigDecimal(record.get("tolerance_percent_min")));
            consumptionMonitoringByProductResult.setTolerancePercentMax(new BigDecimal(record.get("tolerance_percent_max")));
            consumptionMonitoringByProductResult.setToleranceQuantity(new BigDecimal(record.get("tolerance_quantity")));
            consumptionMonitoringByProductResult.setAppointedAmount(new BigDecimal(record.get("quantidade_apontada")));
            consumptionMonitoringByProductResult.setPlannedQuantity(new BigDecimal(record.get("quantidade_planejada")));
            consumptionMonitoringByProductResult.setAmountRevenue(new BigDecimal(record.get("quantidade_receita")));
            consumptionMonitoringByProductResult.setQuantityPerPiece(new BigDecimal(record.get("qtdporpeca")));
            consumptionMonitoringByProductResult.setHelpChainId(Long.valueOf( ( record.get("help_chain_id") == "" ) ? "0" : record.get("help_chain_id") ));
            consumptionMonitoringByProductResult.setEtiquetaId(Long.valueOf( record.get("etiqueta_id")));
            consumptionMonitoringByProductResult.setToleranceMin(new BigDecimal(record.get("tolerance_min")));
            consumptionMonitoringByProductResult.setToleranceMax(new BigDecimal(record.get("tolerance_max")));
        }

        return consumptionMonitoringByProductResult;

    }
}
