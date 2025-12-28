package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.VectorStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Repository
//public interface VectorStoreRepository extends JpaRepository<VectorStore, UUID> {
//
//    @Modifying
//    @Transactional
//    @Query(value = """
//        INSERT INTO vector_store
//        (prod_id, prod_type, content, metadata, emb_vector, created_at, updated_at, created_by, updated_by)
//        VALUES
//        (:prodId, :prodType, :content, CAST(:metadata AS jsonb), CAST(:embVector AS vector),
//         :createdAt, :updatedAt, :createdBy, :updatedBy)
//        """, nativeQuery = true)
//    void insertVectorStore(
//            @Param("prodId") UUID prodId,
//            @Param("prodType") String prodType,
//            @Param("content") String content,
//            @Param("metadata") String metadata,
//            @Param("embVector") String embVector,
//            @Param("createdAt") LocalDateTime createdAt,
//            @Param("updatedAt") LocalDateTime updatedAt,
//            @Param("createdBy") String createdBy,
//            @Param("updatedBy") String updatedBy
//    );
//
//    @Modifying
//    @Transactional
////    @Query("DELETE FROM VectorStore vs WHERE vs.prodId IN :prodIds AND vs.prodType = :prodType")
//    @Query("SELECT vs FROM VectorStore vs WHERE vs.prodId IN :prodIds AND vs.prodType = :prodType")
//    void deleteByProdIdAndProdType(List<Long> prodIds, String prodType);
//}

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStore, UUID> {

    void deleteByProdIdAndProdType(UUID prodId, String prodType);

    void deleteByProdIdInAndProdType(List<UUID> prodIds, String prodType);

    Optional<VectorStore> findByProdIdAndProdType(UUID prodId, String prodType);
}

