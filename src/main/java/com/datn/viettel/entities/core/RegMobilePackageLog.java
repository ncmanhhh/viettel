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
@Table(name = "reg_mobile_package_logs")
public class RegMobilePackageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 13)
    @Column(name = "phone_number_source", nullable = false, length = 13)
    private String phoneNumberSource; // Số điện thoại đăng ký gói cước

    @NotNull
    @Size(max = 13)
    @Column(name = "phone_number_destination", nullable = false, length = 13)
    private String phoneNumberDestination; // Số điện thoại nhận gói cước

    @NotNull
    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    @NotNull
    @Size(max = 20)
    @Column(name = "service_code", nullable = false, length = 20)
    private String serviceCode;

    @NotNull
    @Column(name = "result", nullable = false)
    private Short result;

    @NotNull
    @Size(max = 500)
    @Column(name = "result_message", nullable = false, length = 500)
    private String resultMessage;

    @NotNull
    @Column(name = "register_type", nullable = false)
    private Short registerType;

    @NotNull
    @Column(name = "payment_type", nullable = false)
    private Short paymentType;

    @NotNull
    @Column(name = "price", nullable = false)
    private Integer price;
}