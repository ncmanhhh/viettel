package com.datn.viettel.entities.secondary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFtth {
    Long id;
    String code;
    String nameVi;
    String price;
    String promotionPrice;
    Short priority;
    Integer speed;
    String speedInText;
    String groupName;
    String cycleRaw;
    String cycle;
    String shortDesVi;
    String promotionVi;
}