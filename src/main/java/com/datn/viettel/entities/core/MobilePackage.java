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

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mobile_packages")
public class MobilePackage extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Size(max = 100)
    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Size(max = 100)
    @Column(name = "money_fee", length = 100)
    private String moneyFee;

    @Size(max = 100)
    @Column(name = "data_free", length = 100)
    private String dataFree;

    @Size(max = 255)
    @Column(name = "expire_value")
    private String expireValue;

    @Size(max = 255)
    @Column(name = "expire_type")
    private String expireType;

    @Size(max = 255)
    @Column(name = "short_des_vi")
    private String shortDesVi;

    @Column(name = "priority")
    private Short priority;

    @NotNull
    @Column(name = "status", nullable = false)
    private Short status;

    @NotNull
    @Column(name = "is_embed", nullable = false)
    private Short isEmbed;

    @Size(max = 4000)
    @Column(name = "full_des_vi", length = 4000)
    private String fullDesVi;
}
