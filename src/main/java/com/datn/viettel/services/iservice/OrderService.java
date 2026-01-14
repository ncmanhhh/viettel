package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.request.OrderCreateRequest;

public interface OrderService {

    String createOrder(OrderCreateRequest request);

}

