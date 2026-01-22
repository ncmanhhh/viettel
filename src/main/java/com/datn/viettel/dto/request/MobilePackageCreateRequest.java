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
public class MobilePackageCreateRequest {


    @NotNull(message = "Code is required")
    @JsonProperty("code")
    private String code;

    @JsonProperty("money_fee")
    private String moneyFee;

    @JsonProperty("data_free")
    private String dataFree;

    @JsonProperty("expire_value")
    private String expireValue;

    @JsonProperty("expire_type")
    private String expireType;

    @JsonProperty("short_des_vi")
    private String shortDesVi;

    @JsonProperty("full_des_vi")
    private String fullDesVi;

    @JsonProperty("priority")
    private Short priority;

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private Short status; // 1: Active, 0: Inactive
}
