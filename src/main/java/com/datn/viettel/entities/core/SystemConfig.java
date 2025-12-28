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
//@Table(name = "system_cfgs")
//public class SystemConfig extends BaseEntity {
//    @Id
//    @Column(name = "id", nullable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotNull
//    @Size(max = 100)
//    @Column(name = "code", nullable = false, unique = true, length = 100)
//    private String code;
//
//    @NotNull
//    @Size(max = 255)
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @NotNull
//    @Column(name = "status", nullable = false)
//    private Short status;
//
//    @NotNull
//    @Column(name = "allow_edit", nullable = false)
//    private Short allowEdit;
//
//    @Column(name = "allow_sync")
//    private Short allowSync;
//
//    @Column(name = "require_value")
//    private String requireValue;
//
//    @Column(name = "require_value_min")
//    private Double requireValueMin;
//
//    @Column(name = "require_value_max")
//    private Double requireValueMax;
//}