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

    // ================= CHECK CUSTOMER =================
    @Override
    public boolean checkCustomerInfo(String phoneNumber) {
        if (mockMode) {
            log.info("[MOCK] checkCustomerInfo phone={}", phoneNumber);
            return phoneNumber.matches("^0(3|5|7|8|9)\\d{8}$");
        }

        throw new IllegalStateException("Integration not enabled");
    }

    // ================= REQUEST OTP =================
    @Override
    public void requestOtp(RequestOtpRequest request) {
        if (mockMode) {
            log.info("[MOCK] requestOtp phone={}, otpType={}, serviceCode={}, serviceType={}",
                    request.getPhoneNumber(),
                    request.getOtpType(),
                    request.getServiceCode(),
                    request.getServiceType());
            return;
        }

        throw new IllegalStateException("Integration not enabled");
    }

    // ================= VERIFY OTP =================
    @Override
    public void verifyOtp(VerifyOtpRequest request) {
        if (mockMode) {
            log.info("[MOCK] verifyOtp phone={}, otp={}",
                    request.getPhoneNumber(),
                    request.getOtp());

            // OTP demo cố định
            if (!"123456".equals(request.getOtp())) {
                throw new LogicException(
                        ResponseMessage.Integration.OTP_NOT_MATCHED,
                        Constants.ExecutionCode.INTEGRATION_ERROR
                );
            }
            return;
        }

        throw new IllegalStateException("Integration not enabled");
    }

    // ================= EMBEDDING =================
    @Override
    public float[] embedding(String prompt) {
        if (mockMode) {
            log.info("[MOCK] embedding prompt={}", prompt);

            float[] vector = new float[1536];
            Arrays.fill(vector, 0.01f);
            return vector;
        }

        return aiService.embeddingVectorV2(prompt);
    }

    // ================= PROVINCES =================
    @Override
    public List<Object> getProvinces() {
        if (mockMode) {
            return List.of(
                    Map.of("id", "01", "name", "Hà Nội"),
                    Map.of("id", "79", "name", "TP Hồ Chí Minh")
            );
        }

        throw new IllegalStateException("Integration not enabled");
    }

    // ================= DISTRICTS =================
    @Override
    public List<Object> getDistricts(String provinceId) {
        if (mockMode) {
            if ("01".equals(provinceId)) {
                return List.of(
                        Map.of("id", "001", "name", "Ba Đình"),
                        Map.of("id", "002", "name", "Hoàn Kiếm")
                );
            }
            if ("79".equals(provinceId)) {
                return List.of(
                        Map.of("id", "760", "name", "Quận 1"),
                        Map.of("id", "761", "name", "Quận 3")
                );
            }
            return Collections.emptyList();
        }

        throw new IllegalStateException("Integration not enabled");
    }

    // ================= WARDS =================
    @Override
    public List<Object> getWards(String provinceId, String districtId) {
        if (mockMode) {
            return List.of(
                    Map.of("id", "00001", "name", "Phường 1"),
                    Map.of("id", "00002", "name", "Phường 2")
            );
        }

        throw new IllegalStateException("Integration not enabled");
    }
}
