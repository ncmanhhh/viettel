package com.datn.viettel.repositories.custom;

import com.datn.viettel.entities.core.ConversationMessage;
import com.datn.viettel.repositories.custom.irepository.MessageCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Repository
public class MessageRepositoryImpl implements MessageCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Async("log-async-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logMessage(UUID conversationId, String question, String answer, LocalDateTime time) {
        try {
            ConversationMessage messages = ConversationMessage.builder()
                    .conversationId(conversationId)
                    .question(question)
                    .answer(answer)
                    .sentAt(time)
                    .build();
            entityManager.persist(messages);
        } catch (Exception e) {
            log.error("Error saving message: {}", e.getMessage(), e);
        }
    }

}
