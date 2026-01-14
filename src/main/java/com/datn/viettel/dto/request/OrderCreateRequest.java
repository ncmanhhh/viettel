package com.datn.viettel.dto.request;


import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.utils.anotations.InList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreateRequest {
    @NotNull(message = ResponseMessage.Integration.MISSING_DELIVERY_METHOD)
    @InList(values = {"1", "2"}, targetType = Short.class, message = ResponseMessage.Integration.INVALID_DELIVERY_METHOD)
    Short deliveryMethod; // 1: store pickup, 2: home delivery
    @NotBlank(message = ResponseMessage.Integration.MISSING_PHONE_NUMBER)
    @Length(min = 9, max = 13, message = ResponseMessage.Integration.INVALID_LENGTH_PHONE_NUMBER)
    @Pattern(regexp = "\\d+", message = ResponseMessage.Integration.INVALID_PHONE_NUMBER_FORMAT)
    String phoneNumber;
    @NotBlank(message = ResponseMessage.Integration.MISSING_CUSTOMER_NAME)
    @Length(min = 5, max = 100, message = ResponseMessage.Integration.INVALID_LENGTH_CUSTOMER_NAME)
    String customerName;
    @NotBlank(message = ResponseMessage.Integration.MISSING_PROVINCE)
    @Length(max = 5, message = ResponseMessage.Integration.INVALID_PROVINCE)
    String province;
    @NotBlank(message = ResponseMessage.Integration.MISSING_DISTRICT)
    @Length(max = 5, message = ResponseMessage.Integration.INVALID_DISTRICT)
    String district;
    @NotBlank(message = ResponseMessage.Integration.MISSING_WARD)
    @Length(max = 5, message = ResponseMessage.Integration.INVALID_WARD)
    String ward;
    @NotBlank(message = ResponseMessage.Integration.MISSING_DETAIL_ADDRESS)
    @Length(max = 100, message = ResponseMessage.Integration.INVALID_LENGTH_DETAIL_ADDRESS)
    String detailAddress;
    @NotBlank(message = ResponseMessage.Integration.MISSING_PRODUCT_ID)
    @Length(max = 13, message = ResponseMessage.Integration.INVALID_PRODUCT_ID)
    String productId;
    @NotBlank(message = ResponseMessage.Integration.MISSING_PRODUCT_CODE)
    @Length(max = 100, message = ResponseMessage.Integration.INVALID_PRODUCT_CODE)
    String productCode;
    @NotNull(message = ResponseMessage.Integration.MISSING_SERVICE_TYPE)
    @InList(values = {"1", "2", "3"}, targetType = Short.class, message = ResponseMessage.Integration.INVALID_SERVICE_TYPE)
    Short serviceType; // 1: phone device, 2: ftth, 3: sim
    @NotNull(message = ResponseMessage.Integration.MISSING_QUANTITY)
    @Max(value = 12, message = ResponseMessage.Integration.INVALID_QUANTITY)
    Short quantity;
    @Length(max = 100, message = ResponseMessage.Integration.INVALID_LENGTH_NOTE)
    String note;
    @JsonIgnore
    String productName;
    @JsonIgnore
    BigDecimal price;
    @JsonIgnore
    BigDecimal totalAmount;
}

