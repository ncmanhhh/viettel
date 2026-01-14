package com.datn.viettel.entities.core;

import com.datn.viettel.entities.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chatbots")
public class Chatbot extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "knowledge_id", nullable = false)
    private UUID knowledgeId;

    @NotNull
    @Size(max = 150)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "status", nullable = false)
    private Short status;
}
