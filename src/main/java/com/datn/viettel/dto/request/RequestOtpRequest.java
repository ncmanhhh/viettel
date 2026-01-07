package com.datn.viettel.dto.request;

import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.utils.anotations.InList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestOtpRequest {
    @NotBlank(message = ResponseMessage.Integration.MISSING_PHONE_NUMBER)
    @Length(min = 9, max = 13, message = ResponseMessage.Integration.INVALID_LENGTH_PHONE_NUMBER)
    @Pattern(regexp = "\\d+", message = ResponseMessage.Integration.INVALID_PHONE_NUMBER_FORMAT)
    String phoneNumber;
    @NotNull(message = ResponseMessage.Integration.MISSING_OTP_TYPE)
    @InList(values = {"0", "1", "2"}, targetType = Short.class, message = ResponseMessage.Integration.INVALID_OTP_TYPE)
    Short otpType; // 0: common, 1: u-money, 2: tai-khoan-goc
    @NotBlank(message = ResponseMessage.Integration.MISSING_SERVICE_CODE)
    @Length(max = 20, message = ResponseMessage.Integration.INVALID_SERVICE_CODE)
    String serviceCode;
    @NotNull(message = ResponseMessage.Integration.MISSING_SERVICE_TYPE)
    @InList(values = {"0", "1", "2", "3"}, targetType = Short.class, message = ResponseMessage.Integration.INVALID_SERVICE_TYPE)
    Short serviceType; // 0: mobile package, 1: phone device, 2: ftth, 3: sim
}
