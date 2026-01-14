package com.datn.viettel.repositories.custom;

import com.datn.viettel.dto.request.OrderCreateRequest;
import com.datn.viettel.entities.core.RegOrderLog;
import com.datn.viettel.repositories.custom.irepository.RegOrderLogCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Repository
public class RegOrderLogRepositoryImpl implements RegOrderLogCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Async("log-async-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreateOrder(OrderCreateRequest request, Short result, String resultMessage, LocalDateTime time) {
        try {
            RegOrderLog regOrderLog = RegOrderLog.builder()
                    .phoneNumber(request.getPhoneNumber().trim())
                    .customerName(request.getCustomerName().trim())
                    .provinceCode(request.getProvince().trim())
                    .districtCode(request.getDistrict().trim())
                    .wardCode(request.getWard().trim())
                    .detailAddress(request.getDetailAddress().trim())
                    .productType(request.getServiceType())
                    .productId(request.getProductId().trim())
                    .productCode(request.getProductCode().trim())
                    .quantity(request.getQuantity())
                    .priceUnit(request.getPrice())
                    .totalAmount(request.getTotalAmount())
                    .note(request.getNote() != null ? request.getNote().trim() : "")
                    .result(result)
                    .resultMessage(resultMessage)
                    .requestAt(time)
                    .build();
            entityManager.persist(regOrderLog);
        } catch (Exception e) {
            log.error("Error logging order creation: {}", e.getMessage(), e);
        }
    }

}
