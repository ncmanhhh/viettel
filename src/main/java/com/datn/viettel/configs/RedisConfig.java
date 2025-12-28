package com.datn.viettel.configs;


import com.datn.viettel.utils.DataUtils;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.username}")
    private String username;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int database;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(host, port);
        if (!DataUtils.isNullOrBlank(username)) {
            conf.setUsername(username);
        }
        if (!DataUtils.isNullOrBlank(password)) {
            conf.setPassword(RedisPassword.of(password));
        }
        conf.setDatabase(database);
        SocketOptions socketOptions = SocketOptions.builder()
                .keepAlive(true)
                .tcpNoDelay(true)
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        ClientOptions clientOptions = ClientOptions.builder()
                .autoReconnect(true)
                .pingBeforeActivateConnection(true)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .socketOptions(socketOptions)
                .build();
        GenericObjectPoolConfig<?> pool = new GenericObjectPoolConfig<>();
        pool.setMaxTotal(50);
        pool.setMaxIdle(20);
        pool.setMinIdle(5);
        pool.setTestOnBorrow(true);
        pool.setTestOnReturn(true);
        pool.setTestWhileIdle(true);
        pool.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        pool.setNumTestsPerEvictionRun(3);
        pool.setBlockWhenExhausted(true);
        pool.setMaxWait(Duration.ofSeconds(6));
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .poolConfig(pool)
                .build();
        LettuceConnectionFactory f = new LettuceConnectionFactory(conf, clientConfig);
        f.setValidateConnection(true);
        f.setShareNativeConnection(false);
        f.setEagerInitialization(true);
        return f;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);
        StringRedisSerializer stringSer = new StringRedisSerializer();
        t.setKeySerializer(stringSer);
        t.setHashKeySerializer(stringSer);
        GenericJackson2JsonRedisSerializer jsonSer =
                new GenericJackson2JsonRedisSerializer();
        t.setValueSerializer(jsonSer);
        t.setHashValueSerializer(jsonSer);
        t.afterPropertiesSet();
        return t;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }

}