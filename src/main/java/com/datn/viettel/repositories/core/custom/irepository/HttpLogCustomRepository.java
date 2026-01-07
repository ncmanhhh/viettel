package com.datn.viettel.repositories.core.custom.irepository;

import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;

public interface HttpLogCustomRepository {

    void logHttpRequest(
            String url,
            String method,
            HttpHeaders headers,
            Object requestBody,
            Object responseBody,
            int statusCode,
            LocalDateTime requestAt,
            LocalDateTime responseAt,
            String description
    );

}
