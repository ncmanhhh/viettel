package com.datn.viettel.entities.secondary;

import com.datn.viettel.entities.core.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSim {
    String phoneNumber;
    String simType;
    String numberType;
    String price;
    String promotionPrice;
    String desVi;
}
