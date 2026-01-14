package com.datn.viettel.exceptions;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.exceptions.LogicException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ExecutionResult<Object>> handleLogic(LogicException ex, HttpServletRequest http) {
        ExecutionResult<Object> res = ExecutionResult.builder()
                .data(null)
                .responseCode(ex.getResponseCode())
                .keyMessage(ex.getKeyMessage())
                .description(ex.getMessage())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(http.getRequestURI())
                .build();
        return ResponseEntity.ok(res); // hoặc status theo responseCode
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExecutionResult<Object>> handleAny(Exception ex, HttpServletRequest http) {
        log.error("Unhandled error", ex);
        String key = ResponseMessage.Common.INTERGRATION_SYSTEM_ERROR;
        ExecutionResult<Object> res = ExecutionResult.builder()
                .data(null)
                .responseCode(Constants.ExecutionCode.ERROR)
                .keyMessage(key)
                .description(ResourceMessageConfig.getResourceMessage(key))
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(http.getRequestURI())
                .build();
        return ResponseEntity.ok(res);
    }
}
