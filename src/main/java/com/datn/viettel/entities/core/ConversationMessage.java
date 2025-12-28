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
@Table(name = "conversation_messages")
public class ConversationMessage {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "conversation_id", columnDefinition = "uuid", nullable = false)
    private UUID conversationId;

    @NotNull
    @Size(max = 500)
    @Column(name = "question", nullable = false, length = 500)
    private String question;

    @NotNull
    @Column(name = "answer", nullable = false, length = Integer.MAX_VALUE)
    private String answer;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "token")
    private Integer token;
}