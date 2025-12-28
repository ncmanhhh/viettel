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
@Table(name = "ftth_packages")
public class FtthPackage extends BaseEntity {
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
    @Column(name = "price", length = 100)
    private String price;

    @Size(max = 100)
    @Column(name = "promotion_price", length = 100)
    private String promotionPrice;

    @Size(max = 4000)
    @Column(name = "short_des_en", length = 4000)
    private String shortDesEn;


    @Size(max = 4000)
    @Column(name = "short_des_vi", length = 4000)
    private String shortDesVi;


    @Column(name = "priority")
    private Short priority;

    @NotNull
    @Column(name = "status", nullable = false)
    private Short status;

    @NotNull
    @Column(name = "is_embed", nullable = false)
    private Short isEmbed;

    @Size(max = 200)
    @Column(name = "speed_network", length = 200)
    private String speedNetwork;

    @Column(name = "group_name", length = Integer.MAX_VALUE)
    private String groupName;

    @Column(name = "cycle", length = Integer.MAX_VALUE)
    private String cycle;

    @Column(name = "promotion_en", length = Integer.MAX_VALUE)
    private String promotionEn;

    @Column(name = "promotion_vi", length = Integer.MAX_VALUE)
    private String promotionVi;

}
