package com.datn.viettel.events;

import com.datn.viettel.dto.pojo.ChatSavedEvent;
import com.datn.viettel.repositories.core.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatSavedListener {

    private final MessageRepository messageRepository;

    @Async("chat-async-executor")
    @EventListener
    public void handle(ChatSavedEvent e) {
        try {
            messageRepository.logMessage(
                    e.conversationId(),
                    e.question(),
                    e.answer(),
                    e.requestTime()
            );
        } catch (Exception ex) {
            log.error("Failed to handle ChatSavedEvent: {}", ex.getMessage(), ex);
        }
    }
}
