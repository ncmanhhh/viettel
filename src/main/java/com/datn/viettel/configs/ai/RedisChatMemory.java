package com.datn.viettel.configs.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisChatMemory implements ChatMemory {

    private static final String CHAT_MEMORY_PREFIX = "chat:memory:"; // Prefix key lưu chat memory trong Redis
    private static final long TTL_MINUTES = 60;     // TTL (time-to-live – thời gian sống)
    private static final int MAX_MESSAGES = 30;     // Giữ N message gần nhất (context window – cửa sổ ngữ cảnh)

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatMemory(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @NotNull
    @Override
    public List<Message> get(@NotNull String conversationId) {
        String key = buildKey(conversationId);
        try {
            List<String> jsonList = stringRedisTemplate.opsForList().range(key, 0, -1);
            if (jsonList == null || jsonList.isEmpty()) {
                return new ArrayList<>();
            }

            List<Message> result = new ArrayList<>(jsonList.size());
            for (String json : jsonList) {
                MessageWrapper wrapper = objectMapper.readValue(json, new TypeReference<>() {});
                result.add(wrapper.toMessage());
            }
            return result;

        } catch (Exception e) {
            log.error("Error getting chat memory for conversation: {}", conversationId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void add(@NotNull String conversationId, @NotNull List<Message> messages) {
        String key = buildKey(conversationId);
        try {
            if (messages.isEmpty()) return;

            // Convert từng message -> JSON string
            List<String> payload = new ArrayList<>(messages.size());
            for (Message m : messages) {
                MessageWrapper wrapper = MessageWrapper.fromMessage(m);
                payload.add(objectMapper.writeValueAsString(wrapper));
            }

            // RPUSH (append cuối) - atomic theo từng element
            stringRedisTemplate.opsForList().rightPushAll(key, payload);

            // Giới hạn chỉ giữ MAX_MESSAGES message gần nhất
            // LTRIM -MAX_MESSAGES -1: giữ last N phần tử
            stringRedisTemplate.opsForList().trim(key, -MAX_MESSAGES, -1);

            // Set TTL cho cả list (mỗi lần add sẽ refresh TTL)
            stringRedisTemplate.expire(key, TTL_MINUTES, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("Error saving chat memory for conversation: {}", conversationId, e);
        }
    }

    @Override
    public void clear(@NotNull String conversationId) {
        String key = buildKey(conversationId);
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error clearing chat memory for conversation: {}", conversationId, e);
        }
    }

    private String buildKey(String conversationId) {
        return CHAT_MEMORY_PREFIX + conversationId;
    }
}
