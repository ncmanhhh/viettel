package com.datn.viettel.repositories.core;

import com.datn.viettel.entities.core.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatbotRepository extends JpaRepository<Chatbot, UUID> {
}
