package com.datn.viettel.repositories.custom.irepository;

import com.datn.viettel.dto.request.OrderCreateRequest;

import java.time.LocalDateTime;

public interface RegOrderLogCustomRepository {

    void logCreateOrder(OrderCreateRequest request, Short result, String resultMessage, LocalDateTime time);

}
