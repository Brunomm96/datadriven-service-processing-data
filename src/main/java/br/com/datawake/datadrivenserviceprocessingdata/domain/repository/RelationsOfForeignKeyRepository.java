package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.RelationsOfForeignKey;

@Repository
public interface RelationsOfForeignKeyRepository extends JpaRepository<RelationsOfForeignKey, Long> {

    Optional<RelationsOfForeignKey> findByAccessKey(String accessKey);

    List<RelationsOfForeignKey> findByDwTable(String dwTable);

    @Query(
        nativeQuery = true,
        value = "SELECT TOP 1  a.dw_table_key " +
                "FROM dw_relations_of_foreign_key a " +
                "WHERE a.dw_table = ?1"
    )
    String getDwTableKey( String  dwTable );

}
