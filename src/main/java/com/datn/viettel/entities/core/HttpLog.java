package com.datn.viettel.entities.core;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "http_logs")
public class HttpLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    @NotNull
    @Column(name = "response_at", nullable = false)
    private LocalDateTime responseAt;

    @NotNull
    @Column(name = "url", nullable = false, length = Integer.MAX_VALUE)
    private String url;

    @NotNull
    @Size(max = 20)
    @Column(name = "method", nullable = false, length = 20)
    private String method;

    @NotNull
    @Column(name = "headers", nullable = false, length = Integer.MAX_VALUE)
    private String headers;

    @NotNull
    @Column(name = "request", nullable = false, length = Integer.MAX_VALUE)
    private String request;

    @NotNull
    @Column(name = "response", nullable = false, length = Integer.MAX_VALUE)
    private String response;

    @NotNull
    @Size(max = 5)
    @Column(name = "status_code", nullable = false, length = 5)
    private String statusCode;

    @Size(max = 255)
    @Column(name = "description")
    private String description;
}