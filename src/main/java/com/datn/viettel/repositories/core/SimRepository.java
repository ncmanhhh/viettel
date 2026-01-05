package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.Sim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SimRepository extends JpaRepository<Sim, UUID> {
    List<Sim> findByStatus(Short status);
    List<Sim> findByStatusAndIsEmbed(Short status, Short isEmbed);

    @Query(
            value = """
        SELECT *
        FROM sims
        WHERE (:status IS NULL OR status = :status)
          AND (:isEmbed IS NULL OR is_embed = :isEmbed)
          AND (
                :phone IS NULL
                OR :phone = ''
                OR phone_number ILIKE CONCAT('%', :phone, '%')
          )
          AND (
                :simType IS NULL
                OR :simType = ''
                OR sim_type ILIKE CONCAT('%', :simType, '%')
          )
          AND (
                :numberType IS NULL
                OR :numberType = ''
                OR number_type ILIKE CONCAT('%', :numberType, '%')
          )
          AND (
                :keyword IS NULL
                OR :keyword = ''
                OR des_vi ILIKE CONCAT('%', :keyword, '%')
          )
        ORDER BY created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM sims
        WHERE (:status IS NULL OR status = :status)
          AND (:isEmbed IS NULL OR is_embed = :isEmbed)
          AND (
                :phone IS NULL
                OR :phone = ''
                OR phone_number ILIKE CONCAT('%', :phone, '%')
          )
          AND (
                :simType IS NULL
                OR :simType = ''
                OR sim_type ILIKE CONCAT('%', :simType, '%')
          )
          AND (
                :numberType IS NULL
                OR :numberType = ''
                OR number_type ILIKE CONCAT('%', :numberType, '%')
          )
          AND (
                :keyword IS NULL
                OR :keyword = ''
                OR des_vi ILIKE CONCAT('%', :keyword, '%')
          )
        """,
            nativeQuery = true
    )
    Page<Sim> search(
            @Param("status") Short status,
            @Param("isEmbed") Short isEmbed,
            @Param("phone") String phone,
            @Param("simType") String simType,
            @Param("numberType") String numberType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Modifying
    @Query("""
    UPDATE Sim s
    SET s.status = CASE
        WHEN s.status = 1 THEN 0
        ELSE 1
    END
    WHERE s.id = :id
""")
    int toggleStatus(@Param("id") UUID id);
}
