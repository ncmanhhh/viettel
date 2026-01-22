package com.datn.viettel.configs.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;


// Chỉ định bean để sử dụng RedisChatMemory / Lưu trữ trong Redis thay vì lưu trữ trong bộ nhớ máy
@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatMemory chatMemory(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisChatMemory(stringRedisTemplate, objectMapper);
    }
}
