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
public class RegisterDataRequest {
    @NotBlank(message = ResponseMessage.Integration.MISSING_PHONE_NUMBER)
    @Length(min = 9, max = 13, message = ResponseMessage.Integration.INVALID_LENGTH_PHONE_NUMBER)
    @Pattern(regexp = "\\d+", message = ResponseMessage.Integration.INVALID_PHONE_NUMBER_FORMAT)
    String phoneNumber;
    @NotBlank(message = ResponseMessage.Integration.MISSING_OTP)
    @Length(min = 6, max = 6, message = ResponseMessage.Integration.INVALID_OTP)
    String otp;
    @NotBlank(message = ResponseMessage.Integration.MISSING_SERVICE_CODE)
    @Length(max = 20, message = ResponseMessage.Integration.INVALID_SERVICE_CODE)
    String serviceCode;
    @InList(
            values = {"0", "1"},
            targetType = Short.class,
            message = ResponseMessage.Integration.INVALID_PAYMENT_TYPE
    )
    @NotNull(message = ResponseMessage.Integration.MISSING_PAYMENT_TYPE)
    Short paymentType;
}
