package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.MobilePackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MobilePackageRepository extends JpaRepository<MobilePackage, UUID> {
    List<MobilePackage> findByStatusAndIsEmbed(Short status, Short isEmbed, Pageable pageable);
    List<MobilePackage> findByStatus(Short status);

    @Query("""
        SELECT m FROM MobilePackage m
        WHERE (:status IS NULL OR m.status = :status)
          AND (:isEmbed IS NULL OR m.isEmbed = :isEmbed)
          AND (
               :keyword IS NULL OR
               LOWER(m.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(m.shortDesVi) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<MobilePackage> search(
            @Param("status") Short status,
            @Param("isEmbed") Short isEmbed,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}