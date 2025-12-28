package com.datn.viettel.services.iservice;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;

import java.io.IOException;
import java.util.List;

public interface AIService {
    float[] embeddingVectorV1(String content) throws IOException, InterruptedException;

    float[] embeddingVectorV2(String content);

    // Call chat model (Gemini, OpenAI, etc.)
    ChatResponse callChatModel(List<Message> conversations);
}
