package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class RecordCustomRepositoryImpl implements RecordCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void execProcRecord( String db, String productionUnit ) {

        StringBuilder sb = new StringBuilder();
        sb.append("EXEC ");
        sb.append("[");
        sb.append(db);
        sb.append("].[dbo].[P_DW_INPUT_DEPENDENCIAS_MATERIAIS] ");
        sb.append("@unidade_filial = :productionUnit ");

        entityManager.createNativeQuery(sb.toString())
                .setParameter("productionUnit", productionUnit)
                .executeUpdate();

    }

}
