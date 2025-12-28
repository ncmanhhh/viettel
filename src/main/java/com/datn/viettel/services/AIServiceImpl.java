package com.datn.viettel.services;

import com.datn.viettel.configs.ai.ChatProvider;
import com.datn.viettel.configs.ai.EmbeddingProvider;
import com.datn.viettel.services.iservice.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    private final Environment env;
    private final EmbeddingProvider embeddingProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmbeddingModel embeddingModel;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ChatModel chatModel;

    @Value("${spring.ai.embedding.chunk-size}")
    private String chunkSize;
    @Value("${spring.ai.embedding.overlap-size}")
    private String overlapSize;

    public AIServiceImpl(Environment env,
                         EmbeddingProvider embeddingProvider,
                         ChatProvider chatProvider) {
        this.env = env;
        this.embeddingProvider = embeddingProvider;
        this.embeddingModel = embeddingProvider.getEmbeddingModel();
        this.chatModel = chatProvider.getChatModel();
    }

    @Override
    public float[] embeddingVectorV1(String content) throws IOException, InterruptedException {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        List<String> chunks = splitContentIntoChunks(content, Integer.parseInt(chunkSize), Integer.parseInt(overlapSize));
        if (chunks.isEmpty()) {
            throw new IllegalStateException("No chunks generated from content");
        }

        List<float[]> embeddings = new ArrayList<>(chunks.size());

        // đọc config Gemini
        String location = env.getProperty("spring.ai.vertex.ai.embedding.location", "asia-southeast1");
        String projectId = env.getProperty("spring.ai.vertex.ai.embedding.project-id");
        String model = env.getProperty("spring.ai.vertex.ai.embedding.text.options.model", "gemini-embedding-001");
        int dims = Integer.parseInt(env.getProperty("spring.ai.vertex.ai.embedding.text.options.dimensions", "768"));

        if (projectId == null || projectId.isBlank()) {
            throw new IllegalStateException("Missing config: spring.ai.vertex.ai.embedding.project-id");
        }

        String accessToken = getGoogleAccessToken(); // nhớ thống nhất key credentials-uri trong hàm này

        String vertexEndpoint = String.format(
                "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:predict",
                location, projectId, location, model
        );

        for (String chunk : chunks) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("instances", List.of(Map.of("content", chunk)));
            payload.put("parameters", Map.of("outputDimensionality", dims));

            String jsonPayload = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(vertexEndpoint))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("Gemini embedding failed. status={}, endpoint={}, body={}",
                        response.statusCode(), vertexEndpoint, response.body());
                throw new IllegalStateException("Failed to get embedding from Gemini: " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode valuesNode = root.path("predictions").get(0).path("embeddings").path("values");
            if (valuesNode.isMissingNode() || !valuesNode.isArray()) {
                throw new IllegalStateException("Invalid Gemini response: missing predictions[0].embeddings.values");
            }

            float[] embedding = objectMapper.convertValue(valuesNode, float[].class);
            embeddings.add(embedding);
        }

        return weightedAveragePoolingHybrid(embeddings);
    }
    @Override
    public float[] embeddingVectorV2(String content) {
        try {
            List<String> chunks = splitContentIntoChunks(content, Integer.parseInt(chunkSize), Integer.parseInt(overlapSize));
            List<float[]> embeddings = new ArrayList<>();
            for (String chunk : chunks) {
                EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(chunk));
                embeddings.add(embeddingResponse.getResult().getOutput());
            }
            return weightedAveragePoolingHybrid(embeddings);
        } catch (Exception e) {
            log.error("Error during embedding: {}", e.getMessage(), e);
            throw new RuntimeException("Embedding failed", e);
        }
    }

    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000)
    )
    @Override
    public ChatResponse callChatModel(List<Message> conversations) {
        try {
            org.springframework.ai.chat.prompt.Prompt prompt =
                    new org.springframework.ai.chat.prompt.Prompt(conversations);
            return chatModel.call(prompt);
        } catch (Exception e) {
            log.error("Error during chat call: {}", e.getMessage(), e);
            throw new RuntimeException("Chat call failed", e);
        }
    }

    private String getGoogleAccessToken() throws IOException {
        String credentialsPath = env.getProperty("spring.ai.vertex.ai.embedding.credentials-uri");
        InputStream credentialsStream;
        if (credentialsPath != null && credentialsPath.startsWith("classpath:")) {
            String pathInClasspath = credentialsPath.substring("classpath:".length());
            credentialsStream = getClass().getResourceAsStream(pathInClasspath);
        } else {
            assert credentialsPath != null;
            credentialsStream = new FileInputStream(credentialsPath);
        }
        if (credentialsStream == null) {
            throw new IllegalStateException("Cannot load Google credentials from path: " + credentialsPath);
        }
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }

    private List<String> splitContentIntoChunks(String content, int chunkSize, int overlapSize) {
        if (content == null || content.isEmpty()) return Collections.emptyList();
        if (overlapSize >= chunkSize) {
            throw new IllegalArgumentException("Overlap size must be smaller than chunk size");
        }
        int contentLength = content.length();
        int estimatedChunks = (contentLength + chunkSize - overlapSize - 1) / (chunkSize - overlapSize);
        List<String> chunks = new ArrayList<>(estimatedChunks);
        int start = 0;
        while (start < contentLength) {
            int end = Math.min(contentLength, start + chunkSize);
            chunks.add(content.substring(start, end));
            start += chunkSize - overlapSize;
        }
        return chunks;
    }

    private float[] weightedAveragePooling(List<float[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalStateException("Vectors must not be null or empty");
        }
        int dims = vectors.get(0).length;
        float[] result = new float[dims];
        float invSize = 1.0f / vectors.size();
        for (float[] vector : vectors) {
            if (vector.length != dims) {
                throw new IllegalStateException("All vectors must have the same length");
            }
            for (int j = 0; j < dims; j++) {
                result[j] += vector[j];
            }
        }
        for (int j = 0; j < dims; j++) {
            result[j] *= invSize;
        }
        return result;
    }

    private float[] weightedAveragePoolingOptimized(List<float[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalStateException("Vectors must not be null or empty");
        }
        int dims = vectors.get(0).length;
        float[] result = new float[dims];
        float invSize = 1.0f / vectors.size();
        for (float[] vector : vectors) {
            if (vector.length != dims) {
                throw new IllegalStateException("All vectors must have the same length");
            }
            int i = 0;
            for (; i < dims - 3; i += 4) {
                result[i] += vector[i];
                result[i + 1] += vector[i + 1];
                result[i + 2] += vector[i + 2];
                result[i + 3] += vector[i + 3];
            }
            for (; i < dims; i++) {
                result[i] += vector[i];
            }
        }
        for (int j = 0; j < dims; j++) {
            result[j] *= invSize;
        }
        return result;
    }

    private float[] weightedAveragePoolingParallel(List<float[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalStateException("Vectors must not be null or empty");
        }
        int dims = vectors.get(0).length;
        float[] result = new float[dims];
        float invSize = 1.0f / vectors.size();
        IntStream.range(0, dims).parallel().forEach(j -> {
            float sum = 0.0f;
            for (float[] vector : vectors) {
                sum += vector[j];
            }
            result[j] = sum * invSize;
        });
        return result;
    }

    private float[] weightedAveragePoolingHybrid(List<float[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalStateException("Vectors must not be null or empty");
        }
        if (vectors.size() == 1) {
            return vectors.get(0);
        }
        int vectorCount = vectors.size();
        int dims = vectors.get(0).length;
        if (vectorCount > 1000) {
            return weightedAveragePoolingParallel(vectors);
        } else if (dims >= 1024) {
            return weightedAveragePoolingOptimized(vectors);
        } else {
            return weightedAveragePooling(vectors);
        }
    }

}
