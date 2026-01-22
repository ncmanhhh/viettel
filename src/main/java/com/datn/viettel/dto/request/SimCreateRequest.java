package com.datn.viettel.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimCreateRequest {

    @NotNull(message = "Phone Number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("sim_type")
    private String simType;

    @JsonProperty("number_type")
    private String numberType;

    @JsonProperty("price")
    private String price;

    @JsonProperty("promotion_price")
    private String promotionPrice;

    @JsonProperty("des_vi")
    private String desVi;

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private Short status; // 1: Active, 0: Inactive
}
