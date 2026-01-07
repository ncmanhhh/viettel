package com.datn.viettel.controllers;


import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.request.ChatRequest;
import com.datn.viettel.dto.request.RequestOtpRequest;
import com.datn.viettel.dto.request.VerifyOtpRequest;
import com.datn.viettel.services.iservice.IntegrationService;
import com.datn.viettel.utils.anotations.RateLimit;
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
            @NotBlank(message = ResponseMessage.Common.INVALID_REQUEST)
            @Length(min = 9, max = 13, message = ResponseMessage.Integration.INVALID_LENGTH_PHONE_NUMBER)
            @Pattern(regexp = "\\d+", message = ResponseMessage.Integration.INVALID_PHONE_NUMBER_FORMAT)
            @RequestParam(name = "phoneNumber") String phoneNumber
    ) {
        ExecutionResult<Boolean> responseCheckCustomerInfo = new ExecutionResult<>();
        responseCheckCustomerInfo.setKeyMessage(ResponseMessage.Common.SUCCESS);
        responseCheckCustomerInfo.setData(integrationService.checkCustomerInfo(phoneNumber));
        responseCheckCustomerInfo.setDescription(ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS));
        responseCheckCustomerInfo.setResponseCode(Constants.ExecutionCode.SUCCESS);
        return ResponseEntity.ok().body(responseCheckCustomerInfo);
    }

    @PostMapping("/v1/request-otp")
    @RateLimit(limit = 2, duration = 1, unit = TimeUnit.MINUTES)
    public ResponseEntity<ExecutionResult<Boolean>> requestOtp(@Valid @RequestBody RequestOtpRequest request) {
        ExecutionResult<Boolean> responseRequestOtp = new ExecutionResult<>();
        integrationService.requestOtp(request);
        responseRequestOtp.setKeyMessage(ResponseMessage.Common.SUCCESS);
        responseRequestOtp.setDescription(ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS));
        responseRequestOtp.setResponseCode(Constants.ExecutionCode.SUCCESS);
        responseRequestOtp.setData(true);
        return ResponseEntity.ok().body(responseRequestOtp);
    }

    @PostMapping("/v1/verify-otp")
    @RateLimit(limit = 2, duration = 1, unit = TimeUnit.MINUTES)
    public ResponseEntity<ExecutionResult<Boolean>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        ExecutionResult<Boolean> responseVerifyOtp = new ExecutionResult<>();
        integrationService.verifyOtp(request);
        responseVerifyOtp.setKeyMessage(ResponseMessage.Common.SUCCESS);
        responseVerifyOtp.setDescription(ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS));
        responseVerifyOtp.setData(true);
        responseVerifyOtp.setResponseCode(Constants.ExecutionCode.SUCCESS);
        return ResponseEntity.ok().body(responseVerifyOtp);
    }

    @PostMapping("/v1/embedding")
    public ResponseEntity<ExecutionResult<float[]>> embedding(@Valid @RequestBody ChatRequest request) {
        ExecutionResult<float[]> responseRequestEmbedding = new ExecutionResult<>();
        float[] result = integrationService.embedding(request.getPrompt());
        responseRequestEmbedding.setData(result);
        responseRequestEmbedding.setKeyMessage(ResponseMessage.Common.SUCCESS);
        responseRequestEmbedding.setDescription(ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS));
        responseRequestEmbedding.setResponseCode(Constants.ExecutionCode.SUCCESS);
        return ResponseEntity.ok().body(responseRequestEmbedding);
    }

    @GetMapping("/v1/provinces")
    public ResponseEntity<ExecutionResult<List<Object>>> getProvinces() {
        ExecutionResult<List<Object>> responseGetProvinces = new ExecutionResult<>();
        List<Object> result = integrationService.getProvinces();
        responseGetProvinces.setData(result);
        responseGetProvinces.setKeyMessage(ResponseMessage.Common.SUCCESS);
        responseGetProvinces.setDescription(ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS));
        responseGetProvinces.setResponseCode(Constants.ExecutionCode.SUCCESS);
        return ResponseEntity.ok().body(responseGetProvinces);
    }

    @GetMapping("/v1/districts")
    public ResponseEntity<ExecutionResult<List<Object>>> getDistricts(
            @RequestParam(name = "provinceId", required = false)
            @NotBlank(message = ResponseMessage.Common.INVALID_REQUEST) String provinceId
    ) {
        ExecutionResult<List<Object>> responseGetDistricts = new ExecutionResult<>();
        List<Object> result = integrationService.getDistricts(provinceId);
        responseGetDistricts.setData(result);
        responseGetDistricts.setKeyMessage(ResponseMessage.Common.SUCCESS);
        responseGetDistricts.setDescription(ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS));
        responseGetDistricts.setResponseCode(Constants.ExecutionCode.SUCCESS);
        return ResponseEntity.ok().body(responseGetDistricts);
    }

    @GetMapping("/v1/wards")
    public ResponseEntity<ExecutionResult<List<Object>>> getWards(
            @RequestParam(name = "provinceId", required = false)
            @NotBlank(message = ResponseMessage.Common.INVALID_REQUEST) String provinceId,
            @RequestParam(name = "districtId", required = false)
            @NotBlank(message = ResponseMessage.Common.INVALID_REQUEST) String districtId
    ) {
        ExecutionResult<List<Object>> responseGetWards = new ExecutionResult<>();
        List<Object> result = integrationService.getWards(provinceId, districtId);
        responseGetWards.setData(result);
        responseGetWards.setKeyMessage(ResponseMessage.Common.SUCCESS);
        responseGetWards.setDescription(ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS));
        responseGetWards.setResponseCode(Constants.ExecutionCode.SUCCESS);
        return ResponseEntity.ok().body(responseGetWards);
    }

}
