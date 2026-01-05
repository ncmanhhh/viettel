package com.datn.viettel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimDTO {

    private UUID id;
    private String phoneNumber;
    private String simType;
    private String numberType;

    private String price;
    private String promotionPrice;

    private String desVi;
}