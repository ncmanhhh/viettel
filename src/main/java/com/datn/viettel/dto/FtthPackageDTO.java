package com.datn.viettel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FtthPackageDTO {

    private String id;

    private String code;              // FTTH_HOME1

    private String speed;             // 100Mbps

    private String price;             // 165.000đ

    private String promotionPrice;    // 145.000đ (nếu có)

    private String cycle;             // 6 tháng / 12 tháng

    private String promotion;         // Khuyến mãi lắp đặt, tháng cước...

    private String description;       // Mô tả ngắn gọn

    private String group;             // Cá nhân / Doanh nghiệp (optional)

}
