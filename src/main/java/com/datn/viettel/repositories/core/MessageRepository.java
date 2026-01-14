package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.ConversationMessage;
import com.datn.viettel.repositories.custom.irepository.MessageCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<ConversationMessage, UUID>, MessageCustomRepository {

    List<ConversationMessage> findByConversationIdOrderBySentAtAsc(UUID conversationId);
}