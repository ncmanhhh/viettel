package com.datn.viettel.entities.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversations")
public class Conversation {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Column(name = "chatbot_id", columnDefinition = "uuid")
    private UUID chatbot_id;

    @Size(max = 100)
    @Column(name = "customer", length = 100)
    private String customer;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @NotNull
    @Column(name = "status", nullable = false)
    private Short status;

    @Column(name = "rating")
    private Short rating;

    @NotNull
    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type;

}
