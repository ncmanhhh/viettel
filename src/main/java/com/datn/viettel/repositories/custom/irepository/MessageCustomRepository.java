package com.datn.viettel.repositories.custom.irepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageCustomRepository {

    void logMessage(UUID conversationId, String question, String answer, LocalDateTime time);

}