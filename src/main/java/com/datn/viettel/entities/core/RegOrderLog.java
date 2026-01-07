package com.datn.viettel.entities.core;


import com.datn.viettel.entities.core.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reg_order_logs")
public class RegOrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 13)
    @Column(name = "phone_number", nullable = false, length = 13)
    private String phoneNumber;

    @NotNull
    @Size(max = 100)
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @NotNull
    @Size(max = 3)
    @Column(name = "province_code", nullable = false, length = 3)
    private String provinceCode;

    @Size(max = 3)
    @Column(name = "district_code", nullable = false, length = 3)
    private String districtCode;

    @Size(max = 3)
    @Column(name = "ward_code", nullable = false, length = 3)
    private String wardCode;

    @NotNull
    @Size(max = 100)
    @Column(name = "detail_address", nullable = false, length = 100)
    private String detailAddress;

    @NotNull
    @Column(name = "product_type", nullable = false)
    private Short productType;

    @NotNull
    @Size(max = 100)
    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;

    @NotNull
    @Size(max = 100)
    @Column(name = "product_code", nullable = false, length = 100)
    private String productCode;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Short quantity;

    @NotNull
    @Column(name = "price_unit", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceUnit;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "result", nullable = false)
    private Short result;

    @NotNull
    @Size(max = 500)
    @Column(name = "result_message", nullable = false, length = 500)
    private String resultMessage;

    @NotNull
    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    @Size(max = 100)
    @Column(name = "note", length = 100)
    private String note;
}