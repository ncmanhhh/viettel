//package com.datn.viettel.services;
//
//import com.datn.viettel.common.Constants;
//import com.datn.viettel.dto.request.OrderCreateRequest;
//import com.datn.viettel.repositories.core.RegOrderLogRepository;
//import com.datn.viettel.services.iservice.HttpService;
//import com.datn.viettel.services.iservice.OrderService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Slf4j
//@Service
//public class OrderServiceImpl implements OrderService {
//
//    private final Environment environment;
//    private final HttpService httpService;
//    private final RegOrderLogRepository orderLogRepository;
//
//    @Autowired
//    public OrderServiceImpl(Environment environment, HttpService httpService,
//                             RegOrderLogRepository orderLogRepository) {
//        this.environment = environment;
//        this.httpService = httpService;
//        this.orderLogRepository = orderLogRepository;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public String createOrder(OrderCreateRequest request) {
//
//        LocalDateTime requestTime = LocalDateTime.now();
//
//        /* ================= BUSINESS LOGIC ================= */
//        if (request.getServiceType().equals(Constants.ServiceType.PHONE_DEVICE)) {
//            request.setQuantity((short) 1);
//            request.setPrice(BigDecimal.ZERO);
//            request.setTotalAmount(BigDecimal.ZERO);
//
//        } else if (request.getServiceType().equals(Constants.ServiceType.FTTH_PACKAGE)) {
//
//            ProductFtth productFtth = productRepository.findProductFtth(request.getProductId().trim());
//            if (productFtth == null || productFtth.getPrice() == null) {
//                throw new LogicException(ResponseMessage.Integration.PRODUCT_NOT_FOUND);
//            }
//
//            BigDecimal price = productFtth.getPromotionPrice() != null
//                    ? DataUtils.parseBigDecimal(productFtth.getPromotionPrice())
//                    : DataUtils.parseBigDecimal(productFtth.getPrice());
//
//            request.setProductName(productFtth.getCode());
//            request.setPrice(price);
//            request.setTotalAmount(price.multiply(BigDecimal.valueOf(request.getQuantity())));
//
//        } else if (request.getServiceType().equals(Constants.ServiceType.SIM)) {
//
//            request.setQuantity((short) 1);
//            ProductSim productSim = productRepository.findProductSim(request.getProductId().trim());
//            if (productSim == null || productSim.getPrice() == null) {
//                throw new LogicException(ResponseMessage.Integration.PRODUCT_NOT_FOUND);
//            }
//
//            BigDecimal price = productSim.getPromotionPrice() != null
//                    ? DataUtils.parseBigDecimal(productSim.getPromotionPrice())
//                    : DataUtils.parseBigDecimal(productSim.getPrice());
//
//            request.setProductName(productSim.getPhoneNumber());
//            request.setPrice(price);
//            request.setTotalAmount(price);
//
//        } else {
//            throw new LogicException(ResponseMessage.Integration.PRODUCT_UNSUPPORTED_TYPE);
//        }
//
//        /* ================= MOCK MODE ================= */
//        if (mockMode) {
//            String mockOrderId = "MOCK-" + System.currentTimeMillis();
//
//            log.info("[MOCK][CREATE_ORDER] orderId={}, phone={}, product={}",
//                    mockOrderId,
//                    request.getPhoneNumber(),
//                    request.getProductCode());
//
//            orderLogRepository.logCreateOrder(
//                    request,
//                    Constants.Status.Register.COMPLETED,
//                    mockOrderId,
//                    requestTime
//            );
//
//            return mockOrderId;
//        }
//
//        /* ================= REAL INTEGRATION (PROD) ================= */
//        throw new IllegalStateException("Integration with Shop/Unitel is disabled");
//    }
//
//}
