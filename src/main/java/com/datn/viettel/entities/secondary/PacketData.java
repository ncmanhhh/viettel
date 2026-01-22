package com.datn.viettel.entities.secondary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacketData{
    Long id;
    String code;
    String moneyFee;
    String dataFree;
    String expireValue;
    String expireType;
    String shortDesVi;
    String fullDesVi;
    Short priority;
}
