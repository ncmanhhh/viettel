package com.datn.viettel.dto.pojo;

import java.util.UUID;

public record ChatSavedEvent(
        UUID conversationId,
        String question,
        String answer,
        java.time.LocalDateTime requestTime
) {}

