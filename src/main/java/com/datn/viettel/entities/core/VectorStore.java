package com.datn.viettel.entities.core;

import com.datn.viettel.configs.hibernate.PgVectorFloatArrayType;
import com.datn.viettel.entities.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vector_store")
public class VectorStore extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Column(name = "prod_id")
    private UUID prodId;

    @Size(max = 100)
    @Column(name = "prod_type", length = 100)
    private String prodType;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

    @Column(name = "emb_vector", columnDefinition = "vector(768)")
    @Type(PgVectorFloatArrayType.class)
    private float[] embVector;
}