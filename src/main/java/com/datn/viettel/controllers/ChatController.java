package com.datn.viettel.controllers;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.MessageChatDTO;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.request.ChatRequest;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.services.iservice.ChatService;
import com.datn.viettel.utils.DataUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{chatType}")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chat(
            @PathVariable String chatType,
            @Valid @RequestBody ChatRequest request,
            HttpServletRequest http
    ) {
        Short type = mapChatType(chatType);
        MessageChatDTO result = chatService.chat(request, type);
        return ResponseEntity.ok(success(result, http));
    }

    // ===== Backward compatible endpoints (optional) =====
    @PostMapping("/mobile-packages")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chatMobilePackage(
            @Valid @RequestBody ChatRequest request,
            HttpServletRequest http
    ) {
        MessageChatDTO result = chatService.chat(request, Constants.ServiceType.MOBILE_PACKAGE);
        return ResponseEntity.ok(success(result, http));
    }

    @PostMapping("/ftth-packages")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chatFtthPackage(
            @Valid @RequestBody ChatRequest request,
            HttpServletRequest http
    ) {
        MessageChatDTO result = chatService.chat(request, Constants.ServiceType.FTTH_PACKAGE);
        return ResponseEntity.ok(success(result, http));
    }

    @PostMapping("/sims")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chatSim(
            @Valid @RequestBody ChatRequest request,
            HttpServletRequest http
    ) {
        MessageChatDTO result = chatService.chat(request, Constants.ServiceType.SIM);
        return ResponseEntity.ok(success(result, http));
    }

    private Short mapChatType(String chatType) {
        if (DataUtils.isNullOrBlank(chatType)) {
            throw new LogicException(ResponseMessage.Chat.UNSUPPORTED_CHAT_TYPE);
        }
        return switch (chatType.trim().toLowerCase()) {
            case "mobile-packages", "mobile", "mobi" -> Constants.ServiceType.MOBILE_PACKAGE;
            case "ftth-packages", "ftth" -> Constants.ServiceType.FTTH_PACKAGE;
            case "sims", "sim" -> Constants.ServiceType.SIM;
            default -> throw new LogicException(ResponseMessage.Chat.UNSUPPORTED_CHAT_TYPE);
        };
    }

    private ExecutionResult<MessageChatDTO> success(MessageChatDTO data, HttpServletRequest http) {
        String key = ResponseMessage.Common.SUCCESS;
        return ExecutionResult.<MessageChatDTO>builder()
                .data(data)
                .responseCode(Constants.ExecutionCode.SUCCESS)
                .keyMessage(key)
                .description(ResourceMessageConfig.getResourceMessage(key))
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(http.getRequestURI())
                .build();
    }
}
