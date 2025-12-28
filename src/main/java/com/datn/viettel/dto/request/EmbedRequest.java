package com.datn.viettel.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbedRequest {
    /**
     * type (loại dữ liệu): MOBILE | FTTH | SIM | ALL
     */
    private String type;

    /**
     * dryRun (chạy thử): true = chỉ log, không ghi ES/DB (tuỳ bạn muốn làm sau)
     */
//    private Boolean dryRun;
}
