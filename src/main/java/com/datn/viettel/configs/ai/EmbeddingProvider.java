package com.datn.viettel.configs.ai;

import com.datn.viettel.common.Constants;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingProvider {
    private final GeminiEmbeddingConfig geminiConfig;
    private final EmbeddingModel geminiEmbeddingModel;
//    private String provider;

    @Autowired
    public EmbeddingProvider(Environment environment, GeminiEmbeddingConfig geminiConfig,
                             @Qualifier("geminiEmbeddingModel")
                             EmbeddingModel geminiEmbeddingModel){
        this.geminiConfig = geminiConfig;
        this.geminiEmbeddingModel = geminiEmbeddingModel;
//        provider = environment.getProperty("spring.ai.embedding.provider", Constants.ModelAI.GEMINI);
    }

    public EmbeddingConfig getCurrentEmbeddingConfig() {
        return new EmbeddingConfig(geminiConfig.getModel(), geminiConfig.getDimensions(), Constants.ModelAI.GEMINI);
    }

    public EmbeddingModel getEmbeddingModel() {
        return geminiEmbeddingModel;
    }

    //Hàm record để lưu trữ cấu hình embedding
    public record EmbeddingConfig(String model, String dimensions, String provider) {}
}
