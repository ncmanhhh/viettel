package com.datn.viettel.dto.request.search;

import lombok.*;
import lombok.experimental.FieldDefaults;

import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate fromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate toDate;
    Integer page;
    Integer size;
}
