package com.datn.viettel.configs;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@EnableCaching
public class AsyncConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "systemPrompts", "embeddings", "vectorSearchResults"
        );
    }

    @Bean(name = "log-async-executor")
    public ThreadPoolTaskExecutor loggingExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(8);
        ex.setQueueCapacity(2000);
        ex.setThreadNamePrefix("log-async-");
        ex.setKeepAliveSeconds(30);
        ex.setAllowCoreThreadTimeOut(true);
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setAwaitTerminationSeconds(10);
        ex.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy());
        ex.initialize();
        return ex;
    }

    // Executor for chat processing tasks
    @Bean(name = "chat-async-executor")
    public Executor chatProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50); //Chỉnh số luồng tối thiểu
        executor.setMaxPoolSize(100); // Chỉnh số luồng tối đa
        executor.setQueueCapacity(200); // Chỉnh sức chứa hàng đợi
        executor.setThreadNamePrefix("chat-async-"); // Tiền tố tên luồng
        executor.setKeepAliveSeconds(300); // Thời gian chờ trước khi kết thúc luồng không hoạt động
        executor.setAllowCoreThreadTimeOut(true); // Cho phép luồng cốt lõi hết thời gian chờ
        executor.setWaitForTasksToCompleteOnShutdown(true); // Chờ hoàn thành nhiệm vụ khi tắt
        executor.setAwaitTerminationSeconds(120); // Thời gian chờ tối đa khi tắt
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()); // Chính sách từ chối nhiệm vụ
        executor.initialize(); // Khởi tạo executor
        return executor;
    }

}
