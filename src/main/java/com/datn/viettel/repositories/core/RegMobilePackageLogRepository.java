package com.datn.viettel.repositories.core;


import com.datn.viettel.entities.core.RegMobilePackageLog;
import com.datn.viettel.repositories.custom.irepository.RegMobilePackageLogCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegMobilePackageLogRepository extends JpaRepository<RegMobilePackageLog, Long>, RegMobilePackageLogCustomRepository {

    @Query(value = "SELECT * FROM reg_mobile_package_logs r " +
            "WHERE (:code = '' OR r.service_code LIKE '%' || :code || '%') " +
            "AND (:phoneNumber = '' OR r.phone_number_source LIKE '%' || :phoneNumber || '%' OR r.phone_number_destination LIKE '%' || :phoneNumber || '%') " +
            "AND (CAST(:fromDate AS timestamp) IS NULL OR r.request_at >= CAST(:fromDate AS timestamp)) " +
            "AND (CAST(:toDate AS timestamp) IS NULL OR r.request_at <= CAST(:toDate AS timestamp)) " +
            "AND (CAST(:registerType AS smallint) IS NULL OR r.register_type = CAST(:registerType AS smallint)) " +
            "AND (CAST(:result AS smallint) IS NULL OR r.result = CAST(:result AS smallint)) " +
            "AND (CAST(:paymentType AS smallint) IS NULL OR r.payment_type = CAST(:paymentType AS smallint))",
            nativeQuery = true)
    List<RegMobilePackageLog> findAllReport(
            @Param("code") String code,
            @Param("result") Short result,
            @Param("registerType") Short registerType,
            @Param("paymentType") Short paymentType,
            @Param("phoneNumber") String phoneNumber,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

}