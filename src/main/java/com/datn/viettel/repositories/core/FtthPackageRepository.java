package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.FtthPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FtthPackageRepository extends JpaRepository<FtthPackage, UUID> {
    List<FtthPackage> findByStatusAndIsEmbed(Short status, Short isEmbed, Pageable pageable);
    List<FtthPackage> findByStatus(Short status);

    @Query(
            value = """
        SELECT *
        FROM ftth_packages
        WHERE (:status IS NULL OR status = :status)
          AND (:isEmbed IS NULL OR is_embed = :isEmbed)
          AND (
                :code IS NULL
                OR :code = ''
                OR code ILIKE CONCAT('%', :code, '%')
          )
          AND (
                :speed IS NULL
                OR :speed = ''
                OR speed_in_text ILIKE CONCAT('%', :speed, '%')
          )
          AND (
                :groupName IS NULL
                OR :groupName = ''
                OR group_name ILIKE CONCAT('%', :groupName, '%')
          )
          AND (
                :keyword IS NULL
                OR :keyword = ''
                OR short_des_vi ILIKE CONCAT('%', :keyword, '%')
          )
        ORDER BY priority DESC, created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM ftth_packages
        WHERE (:status IS NULL OR status = :status)
          AND (:isEmbed IS NULL OR is_embed = :isEmbed)
          AND (
                :code IS NULL
                OR :code = ''
                OR code ILIKE CONCAT('%', :code, '%')
          )
          AND (
                :speed IS NULL
                OR :speed = ''
                OR speed_in_text ILIKE CONCAT('%', :speed, '%')
          )
          AND (
                :groupName IS NULL
                OR :groupName = ''
                OR group_name ILIKE CONCAT('%', :groupName, '%')
          )
          AND (
                :keyword IS NULL
                OR :keyword = ''
                OR short_des_vi ILIKE CONCAT('%', :keyword, '%')
          )
        """,
            nativeQuery = true
    )
    Page<FtthPackage> search(
            @Param("status") Short status,
            @Param("isEmbed") Short isEmbed,
            @Param("code") String code,
            @Param("speed") String speed,
            @Param("groupName") String groupName,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    @Modifying
    @Query("""
    UPDATE FtthPackage f
    SET f.status = CASE
        WHEN f.status = 1 THEN 0
        ELSE 1
    END
    WHERE f.id = :id
""")
    int toggleStatus(@Param("id") UUID id);
}
