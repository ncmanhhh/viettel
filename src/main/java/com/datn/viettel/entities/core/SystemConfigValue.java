//package com.datn.viettel.entities.core;
//
//import com.datn.viettel.entities.core.base.BaseEntity;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.*;
//
//@Getter
//@Setter
//@Entity
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "system_cfg_values")
//public class SystemConfigValue extends BaseEntity {
//    @Id
//    @Column(name = "id", nullable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotNull
//    @Column(name = "system_cfg_id", nullable = false)
//    private Long systemConfigId;
//
//    @NotNull
//    @Column(name = "value", nullable = false)
//    private String value;
//
//    @NotNull
//    @Size(max = 255)
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @NotNull
//    @Size(max = 10)
//    @Column(name = "language", nullable = false, length = 10)
//    private String language;
//
//    @NotNull
//    @Column(name = "status", nullable = false)
//    private Short status;
//}
