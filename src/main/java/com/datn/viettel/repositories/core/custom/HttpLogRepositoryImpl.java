package com.datn.viettel.repositories.core.custom;


import com.datn.viettel.entities.core.HttpLog;
import com.datn.viettel.repositories.core.custom.irepository.HttpLogCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Repository
public class HttpLogRepositoryImpl implements HttpLogCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Async("log-async-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logHttpRequest(
            String url,
            String method,
            HttpHeaders headers,
            Object requestBody,
            Object responseBody,
            int statusCode,
            LocalDateTime requestAt,
            LocalDateTime responseAt,
            String description
    ) {
        try {
            HttpLog log = new HttpLog();
            log.setRequestAt(requestAt);
            log.setResponseAt(responseAt);
            log.setUrl(url);
            log.setMethod(method);
            log.setHeaders(headers != null ? headers.toString() : "");
            log.setRequest(requestBody != null ? requestBody.toString() : "");
            log.setResponse(responseBody != null ? responseBody.toString() : "");
            log.setStatusCode(String.valueOf(statusCode));
            log.setDescription(description);
            entityManager.persist(log);
        } catch (Exception e) {
            log.error("Error logging HTTP request: {}", e.getMessage(), e);
        }
    }

}
