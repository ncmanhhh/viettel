package com.datn.viettel.configs.ai;

import com.datn.viettel.common.Constants;
import com.datn.viettel.services.iservice.RedisService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatProvider {
    private final GeminiChatConfig geminiConfig; // Configuration for Gemini chat model
    private final ChatModel geminiChatModel; // Gemini chat model instance

    @Autowired
    public ChatProvider(GeminiChatConfig geminiConfig,
                        @Qualifier("geminiChatModel") ChatModel geminiChatModel) {
        this.geminiConfig = geminiConfig;
        this.geminiChatModel = geminiChatModel;
    }

    public ChatConfig getCurrentChatConfig() {
        return new ChatConfig(
                geminiConfig.getModel(),
                geminiConfig.getMaxTokens(),
                geminiConfig.getTemperature(),
                geminiConfig.getToolNames(),
                Constants.ModelAI.GEMINI
        );
    }

    public ChatModel getChatModel() {
        return geminiChatModel;
    }

    public record ChatConfig(
            String model,
            Integer maxTokens,
            Double temperature,
            List<String> toolNames,
            String provider
    ) {}
}