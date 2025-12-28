package com.datn.viettel;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStartupChecker {

    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void checkRedis() {
        try {
            String pong = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();
            log.info("✅ Redis connected successfully, ping={}", pong);
        } catch (Exception e) {
            log.error("❌ Redis connection failed", e);
        }
    }
}
