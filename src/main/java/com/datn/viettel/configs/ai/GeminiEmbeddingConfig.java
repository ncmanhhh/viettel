package com.datn.viettel.configs.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class GeminiEmbeddingConfig {
    @Value("${spring.ai.vertex.ai.embedding.text.options.model}")
    private String model;
    @Value("${spring.ai.vertex.ai.embedding.text.options.dimensions}")
    private String dimensions;
}
