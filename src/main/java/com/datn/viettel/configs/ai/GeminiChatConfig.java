package com.datn.viettel.configs.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.ai.vertex.ai.gemini.chat.options")
public class GeminiChatConfig {
    private String model;
    @JsonProperty("tool-names")
    private List<String> toolNames = List.of();
    private Integer maxTokens;
    private Double temperature;
}
