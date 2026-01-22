package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Page<Conversation> findByStatus(
            Short status,
            Pageable pageable
    );

    Page<Conversation> findByStatusAndType(
            Short status,
            String type,
            Pageable pageable
    );

    @Query("SELECT c FROM Conversation c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:type IS NULL OR c.type LIKE %:type%) AND " +
            "(:rating IS NULL OR c.rating = :rating)")
    Page<Conversation> findByFilters(
            @Param("status") Short status,
            @Param("type") String type,
            @Param("rating") Integer rating,
            Pageable pageable
    );
}