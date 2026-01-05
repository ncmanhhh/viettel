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
    @Table(name = "sims")
    public class Sim extends BaseEntity {
        @Id
        @UuidGenerator
        @Column(name = "id", columnDefinition = "uuid", nullable = false)
        private UUID id;

        @NotNull
        @Size(max = 100)
        @Column(name = "phone_number", nullable = false, length = 100)
        private String phoneNumber;

        @Size(max = 50)
        @Column(name = "sim_type", length = 50)
        private String simType;

        @Size(max = 100)
        @Column(name = "number_type", length = 100)
        private String numberType;

        @Size(max = 100)
        @Column(name = "price", length = 100)
        private String price;

        @Size(max = 100)
        @Column(name = "promotion_price", length = 100)
        private String promotionPrice;


        @Size(max = 500)
        @Column(name = "des_vi")
        private String desVi;

        @NotNull
        @Column(name = "status", nullable = false)
        private Short status;

        @NotNull
        @Column(name = "is_embed", nullable = false)
        private Short isEmbed;
    }
