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
public class FtthPackageCreateRequest {


    @NotNull(message = "Code is required")
    @JsonProperty("code")
    private String code;

    @JsonProperty("price")
    private String price;

    @JsonProperty("promotion_price")
    private String promotionPrice;

    @JsonProperty("speed_in_text")
    private String speedInText;

    @JsonProperty("speed")
    private int speed;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("cycle")
    private String cycle;

    @JsonProperty("cycle_raw")
    private String cycleRaw;

    @JsonProperty("promotion_vi")
    private String promotionVi;

    @JsonProperty("short_des_vi")
    private String shortDesVi;

    @JsonProperty("priority")
    private Short priority;

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private Short status; // 1: Active, 0: Inactive
}
