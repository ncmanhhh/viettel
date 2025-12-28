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

    @Query(value = "SELECT c.* FROM conversations c JOIN chatbots cb ON c.bot_id = cb.id " +
            "WHERE cb.id = :botId AND c.status <> 0 AND cb.status <> 0 ORDER BY c.started_at DESC ",
            countQuery = "SELECT COUNT(1) FROM conversations c JOIN chatbots cb ON c.bot_id = cb.id " +
                    "WHERE cb.id = :botId AND c.status <> 0 AND cb.status <> 0", nativeQuery = true)
    Page<Conversation> findAll(@Param("botId") UUID botId, Pageable pageable);

}