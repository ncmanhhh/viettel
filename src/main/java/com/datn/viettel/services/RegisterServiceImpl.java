package com.datn.viettel.services;


import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.request.GiveDataRequest;
import com.datn.viettel.dto.request.RegisterDataRequest;
import com.datn.viettel.dto.request.search.MobilePackageLogSearch;
import com.datn.viettel.entities.core.MobilePackage;
import com.datn.viettel.entities.core.RegMobilePackageLog;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.MobilePackageRepository;
import com.datn.viettel.repositories.core.RegMobilePackageLogRepository;
import com.datn.viettel.services.iservice.HttpService;
import com.datn.viettel.services.iservice.RegisterService;
import com.datn.viettel.utils.IntegrationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    private final Environment environment;
    private final HttpService httpService;
    private final RegMobilePackageLogRepository regMobilePackageLogRepository;
    private final MobilePackageRepository mobilePackageRepository;

    @Autowired
    public RegisterServiceImpl(Environment environment, HttpService httpService,
                               RegMobilePackageLogRepository regMobilePackageLogRepository, MobilePackageRepository mobilePackageRepository) {
        this.environment = environment;
        this.httpService = httpService;
        this.regMobilePackageLogRepository = regMobilePackageLogRepository;
        this.mobilePackageRepository = mobilePackageRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public void registerMobilePackage(RegisterDataRequest request) {
        MobilePackage mobilePackage = mobilePackageRepository.findByCode(request.getServiceCode());
        Map<String, Object> requestBody = buildRequestRegister(request);
        Map<String, Object> response = httpService.post(
                environment.getProperty("integration.shop-unitel.url"),
                requestBody,
                Map.class,
                null
        );
        String message = IntegrationUtils.validateResponse(response, "registerMobilePackage");
        if (message.equals(ResponseMessage.Common.SUCCESS)) {
            regMobilePackageLogRepository.logRegisterMobilePackage(
                    request.getPhoneNumber().trim(),
                    request.getPhoneNumber().trim(),
                    LocalDateTime.now(),
                    request.getServiceCode().trim(),
                    Constants.Status.Register.COMPLETED,
                    message,
                    Constants.RegisterType.REGISTER,
                    request.getPaymentType(),
                    Integer.valueOf(mobilePackage.getMoneyFee())
            );
        } else {
            regMobilePackageLogRepository.logRegisterMobilePackage(
                    request.getPhoneNumber().trim(),
                    request.getPhoneNumber().trim(),
                    LocalDateTime.now(),
                    request.getServiceCode().trim(),
                    Constants.Status.Register.FAILED,
                    message,
                    Constants.RegisterType.REGISTER,
                    request.getPaymentType(),
                    Integer.valueOf(mobilePackage.getMoneyFee())
            );
            throw new LogicException(message, Constants.ExecutionCode.INTEGRATION_ERROR);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public void giveMobilePackage(GiveDataRequest request) {
        MobilePackage mobilePackage = mobilePackageRepository.findByCode(request.getServiceCode());
        Map<String, Object> requestBody = buildRequestGift(request);
        Map<String, Object> response = httpService.post(
                environment.getProperty("integration.shop-unitel.url"),
                requestBody,
                Map.class,
                null
        );
        String message = IntegrationUtils.validateResponse(response, "giveMobilePackage");
        if (message.equals(ResponseMessage.Common.SUCCESS)) {
            regMobilePackageLogRepository.logRegisterMobilePackage(
                    request.getPhoneNumberSource().trim(),
                    request.getPhoneNumberDestination().trim(),
                    LocalDateTime.now(),
                    request.getServiceCode().trim(),
                    Constants.Status.Register.COMPLETED,
                    message,
                    Constants.RegisterType.REGISTER,
                    request.getPaymentType(),
                    Integer.valueOf(mobilePackage.getMoneyFee())
            );
        } else {
            regMobilePackageLogRepository.logRegisterMobilePackage(
                    request.getPhoneNumberSource().trim(),
                    request.getPhoneNumberDestination().trim(),
                    LocalDateTime.now(),
                    request.getServiceCode().trim(),
                    Constants.Status.Register.FAILED,
                    message,
                    Constants.RegisterType.REGISTER,
                    request.getPaymentType(),
                    Integer.valueOf(mobilePackage.getMoneyFee())
            );
            throw new LogicException(message, Constants.ExecutionCode.INTEGRATION_ERROR);
        }
    }

    private Map<String, Object> buildRequestRegister(RegisterDataRequest request) {
        Map<String, String> wsRequest = new java.util.HashMap<>(Map.of(
                "isdn", request.getPhoneNumber().trim(),
                "language", LocaleContextHolder.getLocale().getLanguage(),
                "serviceCode", request.getServiceCode().trim(),
                "channel", environment.getProperty("spring.application.code", Constants.Common.APPLICATION_CODE),
                "actionType", "0",
                "otp", request.getOtp().trim()
        ));
        if (request.getPaymentType().equals(Constants.PaymentType.QR)) {
            wsRequest.put("payType", "1");
        }
        return Map.of(
                "wsCode", Objects.requireNonNull(environment.getProperty("integration.shop-unitel.buy-data.func-buy")),
                "wsRequest", wsRequest
        );
    }

    private Map<String, Object> buildRequestGift(GiveDataRequest request) {
        Map<String, String> wsRequest = new java.util.HashMap<>(Map.of(
                "isdn", request.getPhoneNumberDestination().trim(),
                "buyer", request.getPhoneNumberSource().trim(),
                "language", LocaleContextHolder.getLocale().getLanguage(),
                "serviceCode", request.getServiceCode().trim(),
                "channel", environment.getProperty("spring.application.code", Constants.Common.APPLICATION_CODE),
                "actionType", "0",
                "otp", request.getOtp().trim(),
                "isGift", "1"
        ));
        if (request.getPaymentType().equals(Constants.PaymentType.QR)) {
            wsRequest.put("payType", "1");
        }
        return Map.of(
                "wsCode", Objects.requireNonNull(environment.getProperty("integration.shop-unitel.buy-data.func-buy")),
                "wsRequest", wsRequest
        );
    }

    public List<RegMobilePackageLog> getMobilePackageReport(MobilePackageLogSearch request) {
        return regMobilePackageLogRepository.findAllReport(request.getCode(),
                request.getResult(),
                request.getRegisterType(),
                request.getPaymentType(),
                request.getPhoneNumber(),
                Objects.nonNull(request.getFromDate()) ? request.getFromDate().atStartOfDay() : null,
                Objects.nonNull(request.getToDate()) ? request.getToDate().atTime(23, 59, 59) : null
        );
    }

}