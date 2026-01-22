package com.datn.viettel.controllers;

import com.datn.viettel.dto.SimDTO;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.entities.core.Sim;
import com.datn.viettel.services.iservice.SimService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sims")
@RequiredArgsConstructor
public class SimController {

    private final SimService simService;

    @GetMapping
    public ResponseEntity<ExecutionResult<Page<Sim>>> list(
            @RequestParam(required = false) Short status,
            @RequestParam(required = false) Short isEmbed,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String simType,
            @RequestParam(required = false) String numberType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {

        Page<Sim> result = simService.getSims(
                status, isEmbed, phone, simType, numberType, keyword, page, size
        );

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        result,
                        "SUCCESS",
                        "sim.list.success",
                        "Get SIMs successfully",
                        request.getRequestURI()
                )
        );
    }

    @PatchMapping("/toggle-status")
    public ResponseEntity<ExecutionResult<Void>> toggleStatus(
            @RequestBody java.util.List<UUID> ids,
            HttpServletRequest request
    ) {
        simService.toggleStatus(ids);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        null,
                        "SUCCESS",
                        "sim.toggle.status.success",
                        "Toggle SIM status successfully",
                        request.getRequestURI()
                )
        );
    }

    @PostMapping("/create")
    public ResponseEntity<ExecutionResult<Sim>> create(
            @RequestBody @jakarta.validation.Valid com.datn.viettel.dto.request.SimCreateRequest body,
            HttpServletRequest request
    ) {
        Sim result = simService.create(body);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        result,
                        "SUCCESS",
                        "sim.create.success",
                        "Create SIM successfully",
                        request.getRequestURI()
                )
        );
    }
}
