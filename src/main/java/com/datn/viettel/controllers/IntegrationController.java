package com.datn.viettel.controllers;


import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.dto.request.ChatRequest;
import com.datn.viettel.dto.request.RequestOtpRequest;
import com.datn.viettel.dto.request.VerifyOtpRequest;
import com.datn.viettel.services.iservice.IntegrationService;
import com.datn.viettel.utils.anotations.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/integration")
public class IntegrationController {

    private final IntegrationService integrationService;

    @Autowired
    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping("/v1/check-customer-info")
    public ResponseEntity<ExecutionResult<Boolean>> checkCustomerInfo(
            @NotBlank
            @Pattern(regexp = "^0(3|5|7|8|9)\\d{8}$")
            @RequestParam String phoneNumber,
            HttpServletRequest request
    ) {
        Boolean result = integrationService.checkCustomerInfo(phoneNumber);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        result,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }

    @PostMapping("/v1/request-otp")
    @RateLimit(limit = 2, duration = 1, unit = TimeUnit.MINUTES)
    public ResponseEntity<ExecutionResult<Boolean>> requestOtp(
            @Valid @RequestBody RequestOtpRequest requestBody,
            HttpServletRequest request
    ) {
        integrationService.requestOtp(requestBody);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        true,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }

    @PostMapping("/v1/verify-otp")
    @RateLimit(limit = 2, duration = 1, unit = TimeUnit.MINUTES)
    public ResponseEntity<ExecutionResult<Boolean>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest requestBody,
            HttpServletRequest request
    ) {
        integrationService.verifyOtp(requestBody);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        true,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }

    @PostMapping("/v1/embedding")
    public ResponseEntity<ExecutionResult<float[]>> embedding(
            @Valid @RequestBody ChatRequest requestBody,
            HttpServletRequest request
    ) {
        float[] result = integrationService.embedding(requestBody.getPrompt());

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        result,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }

    @GetMapping("/v1/provinces")
    public ResponseEntity<ExecutionResult<List<Object>>> getProvinces(HttpServletRequest request) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        integrationService.getProvinces(),
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }

    @GetMapping("/v1/districts")
    public ResponseEntity<ExecutionResult<List<Object>>> getDistricts(
            @RequestParam String provinceId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        integrationService.getDistricts(provinceId),
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }

    @GetMapping("/v1/wards")
    public ResponseEntity<ExecutionResult<List<Object>>> getWards(
            @RequestParam String provinceId,
            @RequestParam String districtId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        integrationService.getWards(provinceId, districtId),
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }
}

