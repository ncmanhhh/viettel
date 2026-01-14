package com.datn.viettel.services;


import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.request.RequestOtpRequest;
import com.datn.viettel.dto.request.VerifyOtpRequest;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.services.iservice.AIService;
import com.datn.viettel.services.iservice.HttpService;
import com.datn.viettel.services.iservice.IntegrationService;
import com.datn.viettel.utils.DataUtils;
import com.datn.viettel.utils.IntegrationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;
@Slf4j
@Service
public class IntegrationServiceImpl implements IntegrationService {

    private final Environment environment;
    private final HttpService httpService;
    private final AIService aiService;

    @Value("${integration.mock:true}")
    private boolean mockMode;

    @Autowired
    public IntegrationServiceImpl(Environment environment,
                                  HttpService httpService,
                                  AIService aiService) {
        this.environment = environment;
        this.httpService = httpService;
        this.aiService = aiService;
    }

    /* =======================================================
     * CHECK CUSTOMER
     * ======================================================= */
    @Override
    public boolean checkCustomerInfo(String phoneNumber) {

        if (mockMode) {
            log.info("[MOCK][CHECK_CUSTOMER] phone={}", phoneNumber);

            // Giả lập logic nhà mạng
            if (!phoneNumber.matches("^0(3|5|7|8|9)\\d{8}$")) {
                return false;
            }

            // Ví dụ: blacklist số test
            if ("0999999999".equals(phoneNumber)) {
                return false;
            }

            return true;
        }

        throw new IllegalStateException("Integration not enabled");
    }

    /* =======================================================
     * REQUEST OTP
     * ======================================================= */
    @Override
    public void requestOtp(RequestOtpRequest request) {

        if (mockMode) {
            log.info(
                    "[MOCK][REQUEST_OTP] phone={}, otpType={}, serviceCode={}, serviceType={}",
                    request.getPhoneNumber(),
                    request.getOtpType(),
                    request.getServiceCode(),
                    request.getServiceType()
            );

            // Validate giả lập
            if (!checkCustomerInfo(request.getPhoneNumber())) {
                throw new LogicException(
                        ResponseMessage.Integration.CUSTOMER_NOT_FOUND,
                        Constants.ExecutionCode.BUSINESS_ERROR
                );
            }

            // OTP luôn gửi thành công
            log.info("[MOCK][REQUEST_OTP] OTP sent successfully (123456)");
            return;
        }

        throw new IllegalStateException("Integration not enabled");
    }

    /* =======================================================
     * VERIFY OTP
     * ======================================================= */
    @Override
    public void verifyOtp(VerifyOtpRequest request) {

        if (mockMode) {
            log.info("[MOCK][VERIFY_OTP] phone={}, otp={}",
                    request.getPhoneNumber(),
                    request.getOtp());

            // OTP hợp lệ duy nhất
            if (!"123456".equals(request.getOtp())) {
                throw new LogicException(
                        ResponseMessage.Integration.OTP_NOT_MATCHED,
                        Constants.ExecutionCode.INTEGRATION_ERROR
                );
            }

            log.info("[MOCK][VERIFY_OTP] OTP verified successfully");
            return;
        }

        throw new IllegalStateException("Integration not enabled");
    }

    /* =======================================================
     * EMBEDDING (AI)
     * ======================================================= */
    @Override
    public float[] embedding(String prompt) {

        if (mockMode) {
            log.info("[MOCK][EMBEDDING] prompt={}", prompt);

            // Vector 1536 chiều giống OpenAI
            float[] vector = new float[1536];
            for (int i = 0; i < vector.length; i++) {
                vector[i] = (prompt.hashCode() % 100) * 0.0001f;
            }
            return vector;
        }

        return aiService.embeddingVectorV2(prompt);
    }

    /* =======================================================
     * PROVINCES
     * ======================================================= */
    @Override
    public List<Object> getProvinces() {

        if (mockMode) {
            return List.of(
                    Map.of("id", "01", "code", "HN", "name", "Hà Nội"),
                    Map.of("id", "79", "code", "HCM", "name", "TP Hồ Chí Minh"),
                    Map.of("id", "48", "code", "DN", "name", "Đà Nẵng")
            );
        }

        throw new IllegalStateException("Integration not enabled");
    }

    /* =======================================================
     * DISTRICTS
     * ======================================================= */
    @Override
    public List<Object> getDistricts(String provinceId) {

        if (mockMode) {
            return switch (provinceId) {
                case "01" -> List.of(
                        Map.of("id", "001", "name", "Ba Đình"),
                        Map.of("id", "002", "name", "Hoàn Kiếm"),
                        Map.of("id", "003", "name", "Đống Đa")
                );
                case "79" -> List.of(
                        Map.of("id", "760", "name", "Quận 1"),
                        Map.of("id", "761", "name", "Quận 3"),
                        Map.of("id", "762", "name", "Quận 5")
                );
                default -> Collections.emptyList();
            };
        }

        throw new IllegalStateException("Integration not enabled");
    }

    /* =======================================================
     * WARDS
     * ======================================================= */
    @Override
    public List<Object> getWards(String provinceId, String districtId) {

        if (mockMode) {
            return List.of(
                    Map.of("id", "00001", "name", "Phường 1"),
                    Map.of("id", "00002", "name", "Phường 2"),
                    Map.of("id", "00003", "name", "Phường 3")
            );
        }

        throw new IllegalStateException("Integration not enabled");
    }
}
