package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.MobilePackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MobilePackageRepository extends JpaRepository<MobilePackage, UUID> {
    List<MobilePackage> findByStatusAndIsEmbed(Short status, Short isEmbed, Pageable pageable);
    List<MobilePackage> findByStatus(Short status);

    MobilePackage findByCode(@Param("serviceCode") String serviceCode);

    @Query(
            value = """
        SELECT *
        FROM mobile_packages
        WHERE (:status IS NULL OR status = :status)
          AND (:isEmbed IS NULL OR is_embed = :isEmbed)
          AND (
                :code IS NULL
                OR :code = ''
                OR code ILIKE CONCAT('%', :code, '%')
          )
          AND (
                :expire IS NULL
                OR :expire = ''
                OR expire_value ILIKE CONCAT('%', :expire, '%')
                OR expire_type ILIKE CONCAT('%', :expire, '%')
          )
        ORDER BY priority DESC, created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM mobile_packages
        WHERE (:status IS NULL OR status = :status)
          AND (:isEmbed IS NULL OR is_embed = :isEmbed)
          AND (
                :code IS NULL
                OR :code = ''
                OR code ILIKE CONCAT('%', :code, '%')
          )
          AND (
                :expire IS NULL
                OR :expire = ''
                OR expire_value ILIKE CONCAT('%', :expire, '%')
                OR expire_type ILIKE CONCAT('%', :expire, '%')
          )
        """,
            nativeQuery = true
    )
    Page<MobilePackage> search(
            @Param("status") Short status,
            @Param("isEmbed") Short isEmbed,
            @Param("code") String code,
            @Param("expire") String expire,
            Pageable pageable
    );


    @Modifying
    @Query("""
    UPDATE MobilePackage m
    SET m.status = CASE 
        WHEN m.status = 1 THEN 0
        ELSE 1
    END
    WHERE m.id = :id
""")
    int toggleStatus(@Param("id") UUID id);
}