package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.pojo.ChatSavedEvent;
import com.datn.viettel.dto.MessageChatDTO;
import com.datn.viettel.dto.request.ChatRequest;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.ChatbotRepository;
import com.datn.viettel.services.iservice.*;
import com.datn.viettel.utils.DataUtils;
import com.datn.viettel.utils.LanguageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
    private final Environment env;
    private final Executor chatExecutor;
    private final AIService aiService;
    private final ElasticsearchService elasticsearchService;
    private final ChatbotRepository chatbotRepository;
    private final ChatMemory chatMemory;
    private final ConversationService conversationService;
    private final CacheService cacheService;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final Map<String, float[]> embeddingCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> toolContextCache = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher publisher;

    @Autowired
    public ChatServiceImpl(Environment env,
                           @Qualifier("chat-async-executor")
                           Executor chatExecutor,
                           AIService aiService,
                           ElasticsearchService elasticsearchService,
                           ChatbotRepository chatbotRepository,
                           ChatMemory chatMemory,
                           ConversationService conversationService,
                           CacheService cacheService,
                           ChatModel chatModel,
                           ObjectMapper objectMapper,
                           ApplicationEventPublisher publisher) {
        this.env = env;
        this.chatExecutor = chatExecutor;
        this.aiService = aiService;
        this.elasticsearchService = elasticsearchService;
        this.chatbotRepository = chatbotRepository;
        this.chatMemory = chatMemory;
        this.conversationService = conversationService;
        this.cacheService = cacheService;
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    @Override
    public MessageChatDTO chat(ChatRequest request, Short chatType) {
        String originalPrompt = request.getPrompt().trim(); // Lấy đoạn prompt gốc và loại bỏ khoảng trắng thừa
        String language = LanguageUtils.detectLanguage(originalPrompt); // Phát hiện ngôn ngữ của đoạn prompt
        String vectorIndex = getVectorIndexByType(chatType); // Lấy tên vector index dựa trên loại chat
        String conversationId = conversationService.prepareConversationId(request.getConversationId(), chatType, vectorIndex); // Chuẩn bị conversationId
        request.setConversationId(conversationId); // Thiết lập conversationId vào request
        List<Message> existingMessages = prepareExistingMessages(request, originalPrompt, conversationId); // Chuẩn bị các tin nhắn đã tồn tại trong cuộc trò chuyện
        CompletableFuture<String> systemPromptFuture = CompletableFuture
                .supplyAsync(() -> cacheService.getSystemPrompt(chatType, language), chatExecutor); // Lấy system prompt từ cache một cách bất đồng bộ
        CompletableFuture<ChatProcessingData> processingDataFuture = CompletableFuture
                .supplyAsync(() -> processUserQuery(request, chatType), chatExecutor); // Xử lý truy vấn của người dùng một cách bất đồng bộ
        String systemPrompt = systemPromptFuture.join(); // Chờ và lấy system prompt
        ChatProcessingData processingData = processingDataFuture.join(); // Chờ và lấy dữ liệu xử lý
        String toolContext = processingData.toolContext(); // Lấy ngữ cảnh công cụ từ dữ liệu xử lý
        Object moreInfo = processingData.moreInfo(); // Lấy thông tin thêm từ dữ liệu xử lý
        String enhancedPrompt = buildEnhancedPrompt(toolContext, originalPrompt); // Xây dựng prompt nâng cao
        List<Message> newMessages = new ArrayList<>(); // Tạo danh sách tin nhắn mới
        int limit = Integer.parseInt(
                env.getProperty("spring.ai.chat.last-message-knowledge", Constants.AIConfig.LAST_MESSAGE_KNOWLEDGE_DEFAULT)
        ); // Lấy giới hạn số lượng tin nhắn từ cấu hình
        newMessages.add(new UserMessage(enhancedPrompt)); // Thêm tin nhắn người dùng mới vào danh sách
        chatMemory.add(conversationId, newMessages); // Thêm tin nhắn mới vào bộ nhớ cuộc trò chuyện
        existingMessages.addAll(newMessages); // Kết hợp tin nhắn đã tồn tại với tin nhắn mới
        final List<Message> historyForAi;// Tạo danh sách lịch sử cho AI
        if (existingMessages.size() > limit) {
            historyForAi = new ArrayList<>(
                    existingMessages.subList(existingMessages.size() - limit, existingMessages.size()) // Giới hạn số lượng tin nhắn lịch sử
            );
        } else {
            historyForAi = new ArrayList<>(existingMessages); // Sử dụng tất cả tin nhắn đã tồn tại nếu không vượt quá giới hạn
        }
        historyForAi.add(0, new SystemMessage(systemPrompt)); // Thêm system prompt vào đầu danh sách lịch sử
        CompletableFuture<ChatResponse> aiResponseFuture = CompletableFuture
                .supplyAsync(() -> aiService.callChatModel(historyForAi), chatExecutor); // Gọi mô hình chat AI một cách bất đồng bộ
        ChatResponse response = aiResponseFuture.join(); // Chờ và lấy phản hồi từ AI
        try {
            log.info("Full chat response: {}", objectMapper.writeValueAsString(response)); // Ghi log phản hồi đầy đủ từ AI
        } catch (Exception ignore) {}
        MessageChatDTO result = buildMessageChatDTO(response); // Xây dựng DTO tin nhắn chat từ phản hồi
        result.setRequestTime(request.getRequestTime()); // Thiết lập thời gian yêu cầu vào kết quả
        result.setMoreInfo(moreInfo); // Thiết lập thông tin thêm vào kết quả
        result.setConversationId(conversationId); // Thiết lập conversationId vào kết quả
        result.setLanguage(language); // Thiết lập ngôn ngữ vào kết quả
        chatMemory.add(conversationId, List.of(new AssistantMessage(result.getContent()))); // Thêm tin nhắn trợ lý vào bộ nhớ cuộc trò chuyện
        publishEventAsync(conversationId, originalPrompt, result.getContent(), request.getRequestTime()); // Phát sự kiện bất đồng bộ
        return result; // Trả về kết quả
    }


    // Hàm lấy tên vector index dựa trên loại chat
    private String getVectorIndexByType(Short chatType) {
        if (Constants.ServiceType.MOBILE_PACKAGE.equals(chatType)) {
            return env.getProperty("spring.ai.chat.vector-index.mobile-package");
        } else if (Constants.ServiceType.FTTH_PACKAGE.equals(chatType)) {
            return env.getProperty("spring.ai.chat.vector-index.ftth-package");
        } else if (Constants.ServiceType.SIM.equals(chatType)) {
            return env.getProperty("spring.ai.chat.vector-index.sim");
        } else {
            throw new IllegalArgumentException("Unsupported chat type: " + chatType);
        }
    }

    private List<Message> prepareExistingMessages(ChatRequest request, String originalPrompt, String conversationId) {
        List<Message> existingMessages = new ArrayList<>();
        if (!DataUtils.isNullOrBlank(conversationId)) {
            existingMessages = chatMemory.get(conversationId);
            if (!existingMessages.isEmpty()) {
                int size = existingMessages.size();
                int fromIndex = Math.max(0, size - Integer.parseInt(
                        env.getProperty("spring.ai.chat.last-message-prompt", Constants.AIConfig.LAST_MESSAGE_PROMPT_DEFAULT)
                ));
                List<Message> recentMessages = existingMessages.subList(fromIndex, size);
                request.setAdvancedPrompt(
                        recentMessages.stream()
                                .filter(msg -> msg instanceof UserMessage)
                                .map(msg -> "User: " + msg.getText())
                                .collect(Collectors.joining("\n")) + "\nUser: " + originalPrompt
                );
                return recentMessages;
            }
        }
        return existingMessages;
    }

    // Hàm xử lý truy vấn của người dùng và lấy dữ liệu cần thiết
    @SuppressWarnings("unchecked") //Đánh dấu để bỏ qua cảnh báo kiểu không kiểm tra
    private ChatProcessingData processUserQuery(ChatRequest request, Short chatType) {
        // Xác định prompt gốc dựa trên advancedPrompt hoặc prompt thông thường
        // advancedPrompt lấy từ các tin nhắn trước đó nếu có
        // prompt thông thường là prompt mới từ người dùng
        String originalPrompt = DataUtils.isNullOrBlank(request.getAdvancedPrompt()) ? request.getPrompt().trim() : request.getAdvancedPrompt();
        CompletableFuture<Map<String, Object>> aiQueryFuture; // Tương lai bất đồng bộ để lấy dữ liệu truy vấn AI
        if (Constants.ServiceType.MOBILE_PACKAGE.equals(chatType)) {
            aiQueryFuture = CompletableFuture.supplyAsync(() -> {
                String responsePrompt = "";
                Map<String, Object> result = null;
                try {
                    responsePrompt = ChatClient.create(this.chatModel)
                            .prompt(originalPrompt)
                            .system(Constants.SystemPromptQuery.MOBILE_PACKAGE)
                            .call()
                            .content();
                    result = objectMapper.readValue(responsePrompt, Map.class);
                } catch (Exception ignored) {
                } finally {
                    log.info("{} /n {}", responsePrompt, DataUtils.objectToJson(result));
                }
                return result;
            }, chatExecutor);
        } else if (Constants.ServiceType.FTTH_PACKAGE.equals(chatType)) {
            aiQueryFuture = CompletableFuture.supplyAsync(() -> {
                String responsePrompt = "";
                Map<String, Object> result = null;
                try {
                    responsePrompt = ChatClient.create(this.chatModel)
                            .prompt(originalPrompt)
                            .system(Constants.SystemPromptQuery.FTTH_PACKAGE)
                            .call()
                            .content();
                    result = objectMapper.readValue(responsePrompt, Map.class);
                } catch (Exception ignored) {
                } finally {
                    log.info("{} /n {}", responsePrompt, DataUtils.objectToJson(result));
                }
                return result;
            }, chatExecutor);
        } else if (Constants.ServiceType.SIM.equals(chatType)) {
            aiQueryFuture = CompletableFuture.supplyAsync(() -> {
                String responsePrompt = "";
                Map<String, Object> result = null;
                try {
                    responsePrompt = ChatClient.create(this.chatModel)
                            .prompt(originalPrompt)
                            .system(Constants.SystemPromptQuery.SIM)
                            .call()
                            .content();
                    result = objectMapper.readValue(responsePrompt, Map.class);
                } catch (Exception ignored) {
                } finally {
                    log.info("{} /n {}", responsePrompt, DataUtils.objectToJson(result));
                }
                return result;
            }, chatExecutor);
        } else {
            throw new LogicException(ResponseMessage.Chat.UNSUPPORTED_CHAT_TYPE);
        }
        Map<String, Object> mapResponsePrompt = aiQueryFuture.join();
        String index = getVectorIndexByType(chatType);
        Map<String, Object> elasResult = null;
        if (mapResponsePrompt != null) {
            elasResult = buildToolContext(index, new float[0], mapResponsePrompt, chatType);
            log.info("First buildToolContext - moreInfo size: {}", (elasResult.get("moreInfo")) instanceof List<?> list ? list.size() : 0);
        } else {
            float[] promptInVector = embeddingCache.computeIfAbsent(originalPrompt, prompt -> {
                float[] vector = aiService.embeddingVectorV2(prompt);
                if (embeddingCache.size() > 2000) {
                    embeddingCache.entrySet().stream()
                            .limit(embeddingCache.size() / 4)
                            .map(Map.Entry::getKey)
                            .forEach(embeddingCache::remove);
                }
                return vector;
            });
            elasResult = buildToolContext(index, promptInVector, null, chatType);
            log.info("Second buildToolContext - moreInfo size: {}", (elasResult.get("moreInfo")) instanceof List<?> list ? list.size() : 0);
        }
        return new ChatProcessingData(
                String.valueOf(elasResult.get("contentFull")),
                elasResult.get("moreInfo")
        );
    }


    private Map<String, Object> buildToolContext(String index, float[] promptInVector, Map<String, Object> query, Short chatType) {
        String cacheKey = index + "_" + Arrays.hashCode(promptInVector) + "_" + // Tạo khóa cache dựa trên index, vector nhúng và truy vấn
                (query != null ? query.hashCode() : 0);
        Map<String, Object> cachedResult = toolContextCache.get(cacheKey); // Kiểm tra xem kết quả đã được lưu trong cache chưa
        if (cachedResult != null) {
            return cachedResult; // Nếu có, trả về kết quả từ cache
        }

        int vectorTop = getVectorTopByType(chatType); // Lấy số lượng vector top dựa trên loại chat
        Object vectorSearchResult = elasticsearchService.searchByVector(
                index, promptInVector,
                vectorTop,
                Double.parseDouble(env.getProperty("spring.ai.chat.vector-score", Constants.AIConfig.VECTOR_SCORE_DEFAULT)),
                "contentVector", query); // Thực hiện tìm kiếm vector trong Elasticsearch
        if (vectorSearchResult == null) {
            Map<String, Object> emptyResult = Map.of("contentFull", "", "moreInfo", Collections.emptyList());
            toolContextCache.put(cacheKey, emptyResult);
            return emptyResult; // Nếu không có kết quả, trả về kết quả rỗng
        }
        try {
            Map<String, Object> vectorResult = (Map<String, Object>) objectMapper.convertValue(vectorSearchResult, Map.class);
            Map<String, Object> hitsMap = objectMapper.convertValue(
                    vectorResult.get("hits"), new TypeReference<>() {
                    });
            List<Map<String, Object>> hitsList = objectMapper.convertValue(
                    hitsMap.get("hits"), new TypeReference<>() {
                    });
            String contentFull = hitsList.stream()
                    .map(hit -> (Map<String, Object>) hit.get("_source"))
                    .map(source -> (String) source.get("contentFull"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("; "));
            List<Object> moreInfo = hitsList.stream()
                    .map(hit -> (Map<String, Object>) hit.get("_source"))
                    .map(source -> source.get("content"))
                    .filter(Objects::nonNull)
                    .toList();
            Map<String, Object> result = Map.of("contentFull", contentFull, "moreInfo", moreInfo);
            if (toolContextCache.size() < 500) {
                toolContextCache.put(cacheKey, result);
            }
            return result;
        } catch (Exception e) {
            log.error("Error processing vector search results", e);
            Map<String, Object> errorResult = Map.of("contentFull", "", "moreInfo", Collections.emptyList());
            toolContextCache.put(cacheKey, errorResult);
            return errorResult;
        }
    }

    // Hàm lấy số lượng vector top dựa trên loại chat
    private int getVectorTopByType(Short chatType) {
        if (Constants.ServiceType.MOBILE_PACKAGE.equals(chatType)) {
            return Integer.parseInt(
                    env.getProperty("spring.ai.chat.vector-top.mobile-package", Constants.AIConfig.VECTOR_TOP_DEFAULT)
            );
        } else if (Constants.ServiceType.FTTH_PACKAGE.equals(chatType)) {
            return Integer.parseInt(
                    env.getProperty("spring.ai.chat.vector-top.ftth-package", Constants.AIConfig.VECTOR_TOP_DEFAULT)
            );
        } else if (Constants.ServiceType.SIM.equals(chatType)) {
            return Integer.parseInt(
                    env.getProperty("spring.ai.chat.vector-top.sim", Constants.AIConfig.VECTOR_TOP_DEFAULT)
            );
        } else {
            return Integer.parseInt(Constants.AIConfig.VECTOR_TOP_DEFAULT);
        }
    }


    // Hàm xây dựng prompt nâng cao với ngữ cảnh công cụ và prompt của người dùng
    private String buildEnhancedPrompt(String toolContext, String userPrompt) {
        String language = LanguageUtils.detectLanguage(userPrompt); // Phát hiện ngôn ngữ của prompt người dùng

        // Xây dựng prompt nâng cao với ngữ cảnh: Luôn trả lời cùng ngôn ngữ với câu hỏi của người dùng
        return "Important: Always respond in the same language as the user's question. " +
                " If the user's language is unclear or generic, respond in " + language + "." +
                " \n Knowledge Base: [" + toolContext.trim() + "]" +
                " \n User question: [" + userPrompt.trim() + "] ";
    }

    //Hàm xây dựng DTO tin nhắn chat từ phản hồi của mô hình chat AI
    private MessageChatDTO buildMessageChatDTO(ChatResponse response) {
        if (Objects.nonNull(response)) {
            String fullText = response.getResults().stream()
                    .map(r -> r.getOutput().getText())
                    .collect(Collectors.joining());
            return MessageChatDTO.builder()
                    .responseTime(LocalDateTime.now())
                    .role(Constants.Conversation.Role.ASSISTANT) // Vai trò của người trả lời là trợ lý ảo
                    .content(fullText)
                    .build();
        }
        return new MessageChatDTO();
    }

    //    @Async("chat-async-executor")
    protected void publishEventAsync(String conversationId, String userPrompt, String aiResponse, LocalDateTime requestTime) {
        try {
            UUID conversationUuid = UUID.fromString(conversationId);
            publisher.publishEvent(new ChatSavedEvent(conversationUuid, userPrompt, aiResponse, requestTime));
        } catch (Exception e) {
            log.error("Failed to publish chat event", e);
        }
    }

    private record ChatProcessingData(String toolContext, Object moreInfo) {
    }
}
