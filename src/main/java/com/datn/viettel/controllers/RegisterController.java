package com.datn.viettel.controllers;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.dto.request.search.MobilePackageLogSearch;
import com.datn.viettel.services.iservice.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/registers")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/logs/mobile-packages")
    public ResponseEntity<ExecutionResult<Map<String, Object>>> getMobilePackageReport(
            @ModelAttribute MobilePackageLogSearch search,
            HttpServletRequest http
    ) {
        Map<String, Object> data = registerService.getMobilePackageReport(search);
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        data,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        http.getRequestURI()
                )
        );
    }
}
