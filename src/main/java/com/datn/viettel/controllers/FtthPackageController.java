package com.datn.viettel.controllers;

import com.datn.viettel.dto.FtthPackageDTO;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.entities.core.FtthPackage;
import com.datn.viettel.services.iservice.FtthPackageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ftth")
@RequiredArgsConstructor
public class FtthPackageController {

    private final FtthPackageService ftthPackageService;

    @GetMapping
    public ResponseEntity<ExecutionResult<Page<FtthPackage>>> list(
            @RequestParam(required = false) Short status,
            @RequestParam(required = false) Short isEmbed,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String speed,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {

        Page<FtthPackage> result =
                ftthPackageService.getFtthPackages(
                        status, isEmbed, code, speed, groupName, keyword, page, size
                );

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        result,
                        "SUCCESS",
                        "ftth.package.list.success",
                        "Get FTTH packages successfully",
                        request.getRequestURI()
                )
        );
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ExecutionResult<Void>> toggleStatus(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {

        ftthPackageService.toggleStatus(id);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        null,
                        "SUCCESS",
                        "ftth.package.toggle.status.success",
                        "Toggle FTTH package status successfully",
                        request.getRequestURI()
                )
        );
    }


    @GetMapping("/{id}")
    public FtthPackageDTO detail(@PathVariable UUID id) {
        return ftthPackageService.getById(id);
    }
}
