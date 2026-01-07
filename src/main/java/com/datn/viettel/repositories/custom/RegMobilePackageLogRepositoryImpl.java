package com.datn.viettel.repositories.custom;


import com.datn.viettel.repositories.custom.irepository.RegMobilePackageLogCustomRepository;
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
public class RegMobilePackageLogRepositoryImpl implements RegMobilePackageLogCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Async("log-async-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRegisterMobilePackage(String phoneNumberSource,
                                         String phoneNumberDestination,
                                         LocalDateTime requestAt,
                                         String serviceCode,
                                         Short result,
                                         String resultMessage,
                                         Short registerType,
                                         Short paymentType,
                                         Integer price) {
        try {
            String query = "INSERT INTO reg_mobile_package_logs (phone_number_source, phone_number_destination, request_at, service_code, result, result_message, register_type, payment_type, price) "
                    + "VALUES (:phoneNumberSource, :phoneNumberDestination, :requestAt, :serviceCode, :result, :resultMessage, :registerType, :paymentType, :price)";
            entityManager.createNativeQuery(query)
                    .setParameter("phoneNumberSource", phoneNumberSource)
                    .setParameter("phoneNumberDestination", phoneNumberDestination)
                    .setParameter("requestAt", requestAt)
                    .setParameter("serviceCode", serviceCode)
                    .setParameter("result", result)
                    .setParameter("resultMessage", resultMessage)
                    .setParameter("registerType", registerType)
                    .setParameter("paymentType", paymentType)
                    .setParameter("price", price)
                    .executeUpdate();
        } catch (Exception e) {
            log.error("Error saving register data for phone {}: {}", phoneNumberSource, e.getMessage(), e);
        }
    }

}
