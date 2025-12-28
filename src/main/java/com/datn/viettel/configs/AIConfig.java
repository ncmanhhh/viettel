package com.datn.viettel.configs;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.cloud.vertexai.VertexAI;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class AIConfig {

    @Value("${spring.ai.vertex.ai.embedding.location}")
    private String geminiEmbeddingLocation;

    @Value("${spring.ai.vertex.ai.embedding.project-id}")
    private String geminiEmbeddingProjectId;

    @Value("${spring.ai.vertex.ai.embedding.text.options.model}")
    private String geminiEmbeddingModel;

    @Value("${spring.ai.vertex.ai.embedding.text.options.dimensions}")
    private int geminiEmbeddingDimensions;

    @Value("${spring.ai.vertex.ai.gemini.chat.options.model}")
    private String geminiChatModel;

    @Value("${spring.ai.vertex.ai.gemini.chat.options.temperature}")
    private double geminiChatTemperature;

    @Value("${spring.ai.vertex.ai.gemini.chat.options.max-tokens}")
    private int geminiChatMaxTokens;

    @Value("${spring.ai.vertex.ai.credentials-uri}")
    private String googleCredentials;

    private List<String> geminiChatToolNames = Collections.emptyList();

    private final ToolCallingManager toolCallingManager; // Công cụ hỗ trợ gọi các tool bên ngoài
    private final RetryTemplate retryTemplate; // Mẫu retry để xử lý các thao tác có thể thất bại
    private final ObservationRegistry observationRegistry; // Đăng ký quan sát để theo dõi và ghi lại các số liệu

    public AIConfig(ToolCallingManager toolCallingManager, RetryTemplate retryTemplate,
                    ObservationRegistry observationRegistry) {
        this.toolCallingManager = toolCallingManager;
        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
    }

    private GoogleCredentials getCredentials(String resource) throws IOException {
        InputStream credentialsStream;
        if (resource != null && resource.startsWith("classpath:")) {
            credentialsStream = getClass().getResourceAsStream(resource.substring("classpath:".length()));
        } else {
            if (resource == null || resource.isBlank()) {
                throw new IllegalStateException("spring.ai.vertex.ai.credentials-uri is blank");
            }
            credentialsStream = new FileInputStream(resource);
        }
        if (credentialsStream == null) {
            throw new IllegalStateException("Cannot load Google credentials from path: " + resource);
        }
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        return credentials;
    }

    // ===== Embedding connection details
    @Bean
    public VertexAiEmbeddingConnectionDetails vertexAiEmbeddingConnectionDetails() throws IOException {
        String endpoint = String.format("%s-aiplatform.googleapis.com:443", geminiEmbeddingLocation);

        return VertexAiEmbeddingConnectionDetails.builder()
                .projectId(geminiEmbeddingProjectId)
                .location(geminiEmbeddingLocation)
                .predictionServiceSettings(
                        PredictionServiceSettings.newBuilder()
                                .setEndpoint(endpoint)
                                .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials(googleCredentials)))
                                .build()
                )
                .build();
    }

    // ===== VertexAI client (for chat)
    @Bean
    public VertexAI vertexAI() throws IOException {
        return new VertexAI.Builder()
                .setProjectId(geminiEmbeddingProjectId)
                .setLocation(geminiEmbeddingLocation)
                .setCredentials(getCredentials(googleCredentials))
                .build();
    }

    // ===== Gemini EmbeddingModel
    @Bean
    @Qualifier("geminiEmbeddingModel")
    public EmbeddingModel vertexAiMultimodalEmbeddingModel(VertexAiEmbeddingConnectionDetails connectionDetails) {
        VertexAiTextEmbeddingOptions options = VertexAiTextEmbeddingOptions.builder()
                .model(geminiEmbeddingModel)
                .dimensions(geminiEmbeddingDimensions)
                .build();
        return new VertexAiTextEmbeddingModel(connectionDetails, options);
    }

    // ===== Gemini ChatModel
    @Bean
    @Qualifier("geminiChatModel")
    public ChatModel vertexAiChatModel(VertexAI vertexAI) {
        Set<String> geminiChatToolNameSet = new HashSet<>(geminiChatToolNames);
        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
                .model(geminiChatModel)
                .temperature(geminiChatTemperature)
                .maxOutputTokens(geminiChatMaxTokens)
                .toolNames(geminiChatToolNameSet)
                .build();
        return new VertexAiGeminiChatModel(
                vertexAI,
                options,
                toolCallingManager,
                retryTemplate,
                observationRegistry);
    }
}
//package com.datn.viettel.configs;
//
//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
//import com.google.cloud.vertexai.VertexAI;
//import io.micrometer.observation.ObservationRegistry;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.model.tool.ToolCallingManager;
//import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
//import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
//import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
//import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
//import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.retry.support.RetryTemplate;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Configuration
//public class AIConfig {
//
//    @Value("${spring.ai.vertex.ai.embedding.location}")
//    private String geminiEmbeddingLocation;
//
//    @Value("${spring.ai.vertex.ai.embedding.project-id}")
//    private String geminiEmbeddingProjectId;
//
//    @Value("${spring.ai.vertex.ai.embedding.text.options.model}")
//    private String geminiEmbeddingModel;
//
//    @Value("${spring.ai.vertex.ai.embedding.text.options.dimensions}")
//    private int geminiEmbeddingDimensions;
//
//    @Value("${spring.ai.vertex.ai.gemini.chat.options.model}")
//    private String geminiChatModel;
//
//    @Value("${spring.ai.vertex.ai.gemini.chat.options.temperature}")
//    private double geminiChatTemperature;
//
//    @Value("${spring.ai.vertex.ai.gemini.chat.options.max-tokens}")
//    private int geminiChatMaxTokens;
//
//    @Value("${spring.ai.vertex.ai.credentials-uri}")
//    private String googleCredentials;
//    @Value("${spring.ai.vertex.ai.gemini.chat.options.tool-names:}")
//    private List<String> geminiChatToolNames = Collections.emptyList();
//
//    private final ToolCallingManager toolCallingManager;  // Công cụ hỗ trợ gọi các tool bên ngoài
//    private final RetryTemplate retryTemplate; // Mẫu retry để xử lý các thao tác có thể thất bại
//    private final ObservationRegistry observationRegistry; // Đăng ký quan sát để theo dõi và ghi lại các số liệu
//
//    public AIConfig(ObjectProvider<ToolCallingManager> toolCallingManager, RetryTemplate retryTemplate,
//                    ObservationRegistry observationRegistry) {
//        this.toolCallingManager = toolCallingManager.getIfAvailable();
//        this.retryTemplate = retryTemplate;
//        this.observationRegistry = observationRegistry;
//    }
//
//    private GoogleCredentials getCredentials(String resource) throws IOException {
//        InputStream credentialsStream;
//        if (resource != null && resource.startsWith("classpath:")) {
//            credentialsStream = getClass().getResourceAsStream(resource.substring("classpath:".length()));
//        } else {
//            if (resource == null || resource.isBlank()) {
//                throw new IllegalStateException("spring.ai.vertex.ai.credentials-uri is blank");
//            }
//            credentialsStream = new FileInputStream(resource);
//        }
//        if (credentialsStream == null) {
//            throw new IllegalStateException("Cannot load Google credentials from path: " + resource);
//        }
//        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
//                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//        credentials.refreshIfExpired();
//        return credentials;
//    }
//
//    // ===== Embedding connection details
//    @Bean
//    public VertexAiEmbeddingConnectionDetails vertexAiEmbeddingConnectionDetails() throws IOException {
//        String endpoint = String.format("%s-aiplatform.googleapis.com:443", geminiEmbeddingLocation);
//
//        return VertexAiEmbeddingConnectionDetails.builder()
//                .projectId(geminiEmbeddingProjectId)
//                .location(geminiEmbeddingLocation)
//                .predictionServiceSettings(
//                        PredictionServiceSettings.newBuilder()
//                                .setEndpoint(endpoint)
//                                .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials(googleCredentials)))
//                                .build()
//                )
//                .build();
//    }
//
//    // ===== VertexAI client (for chat)
//    @Bean
//    public VertexAI vertexAI() throws IOException {
//        return new VertexAI.Builder()
//                .setProjectId(geminiEmbeddingProjectId)
//                .setLocation(geminiEmbeddingLocation)
//                .setCredentials(getCredentials(googleCredentials))
//                .build();
//    }
//
//    // ===== Gemini EmbeddingModel
//    @Bean
//    @Qualifier("geminiEmbeddingModel")
//    public EmbeddingModel vertexAiMultimodalEmbeddingModel(VertexAiEmbeddingConnectionDetails connectionDetails) {
//        VertexAiTextEmbeddingOptions options = VertexAiTextEmbeddingOptions.builder()
//                .model(geminiEmbeddingModel)
//                .dimensions(geminiEmbeddingDimensions)
//                .build();
//        return new VertexAiTextEmbeddingModel(connectionDetails, options);
//    }
//
//    // ===== Gemini ChatModel
//    @Bean
//    @Qualifier("geminiChatModel")
//    public ChatModel vertexAiChatModel(VertexAI vertexAI) {
//        Set<String> geminiChatToolNameSet = new HashSet<>(geminiChatToolNames);
//        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
//                .model(geminiChatModel)
//                .temperature(geminiChatTemperature)
//                .maxOutputTokens(geminiChatMaxTokens)
//                .toolNames(geminiChatToolNameSet)
//                .build();
//        return new VertexAiGeminiChatModel(
//                vertexAI,
//                options,
//                toolCallingManager,
//                retryTemplate,
//                observationRegistry);
//    }
//}
