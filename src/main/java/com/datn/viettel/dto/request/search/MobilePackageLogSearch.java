package com.datn.viettel.dto.request.search;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobilePackageLogSearch {
    String code;
    Short result;
    Short registerType;
    Short paymentType;
    String phoneNumber;
    LocalDate fromDate;
    LocalDate toDate;
}
