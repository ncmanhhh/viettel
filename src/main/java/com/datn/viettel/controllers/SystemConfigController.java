package com.datn.viettel.controllers;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.SystemConfigValueDTO;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.services.iservice.SystemConfigService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system-config")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    @GetMapping
    public ResponseEntity<ExecutionResult<List<SystemConfigValueDTO>>> getSystemConfigValue(
            @RequestParam(name = "systemConfigCode", defaultValue = "") String systemConfigCode,
            HttpServletRequest request
    ) {

        if (StringUtils.isBlank(systemConfigCode)) {
            throw new LogicException(ResponseMessage.SystemConfig.MISSING_CODE);
        }

        List<SystemConfigValueDTO> data =
                systemConfigService.getSystemConfigValues(systemConfigCode.trim());

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        data,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }

    @GetMapping("/all")
    public ResponseEntity<ExecutionResult<List<SystemConfigValueDTO>>> getAllSystemConfigValues(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        systemConfigService.getAllSystemConfigValues(),
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        request.getRequestURI()
                )
        );
    }
}