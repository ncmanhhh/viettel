package com.datn.viettel.controllers;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.dto.request.RegisterDataRequest;
import com.datn.viettel.entities.core.MobilePackage;
import com.datn.viettel.services.iservice.MobilePackageService;
import com.datn.viettel.services.iservice.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/mobile-packages")
@RequiredArgsConstructor
public class MobilePackageController {

    private final MobilePackageService mobilePackageService;
    private final RegisterService registerService;

    @GetMapping
    public ResponseEntity<ExecutionResult<Page<MobilePackage>>> list(
            @RequestParam(required = false) Short status,
            @RequestParam(required = false) Short isEmbed,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String expire,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {

        Page<MobilePackage> result = mobilePackageService.getMobilePackages(
                status, isEmbed, code, expire, page, size
        );

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        result,
                        "SUCCESS",
                        "mobile.package.list.success",
                        "Get mobile packages successfully",
                        request.getRequestURI()
                )
        );
    }

    @PostMapping("/register-data")
    public ResponseEntity<ExecutionResult<Boolean>> registerData(
            @Valid @RequestBody RegisterDataRequest request,
            HttpServletRequest httpRequest
    ) {
        registerService.registerMobilePackage(request);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        true,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        httpRequest.getRequestURI()
                )
        );
    }



    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ExecutionResult<Void>> toggleStatus(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {

        mobilePackageService.toggleStatus(id);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        null,
                        "SUCCESS",
                        "mobile.package.toggle.status.success",
                        "Toggle mobile package status successfully",
                        request.getRequestURI()
                )
        );
    }

}