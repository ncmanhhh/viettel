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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ================== MAIN CHAT ENDPOINT ==================
    @PostMapping("/{chatType}")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chat(
            @PathVariable String chatType,
            @Valid @RequestBody ChatRequest request
    ) {
        Short type = mapChatType(chatType);
        MessageChatDTO result = chatService.chat(request, type);
        return buildSuccessResponse(result);
    }

    // ================== BACKWARD COMPATIBLE ==================
    @PostMapping("/mobile-packages")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chatMobilePackage(
            @Valid @RequestBody ChatRequest request
    ) {
        MessageChatDTO result =
                chatService.chat(request, Constants.ServiceType.MOBILE_PACKAGE);
        return buildSuccessResponse(result);
    }

    @PostMapping("/ftth-packages")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chatFtthPackage(
            @Valid @RequestBody ChatRequest request
    ) {
        MessageChatDTO result =
                chatService.chat(request, Constants.ServiceType.FTTH_PACKAGE);
        return buildSuccessResponse(result);
    }

    @PostMapping("/sims")
    public ResponseEntity<ExecutionResult<MessageChatDTO>> chatSim(
            @Valid @RequestBody ChatRequest request
    ) {
        MessageChatDTO result =
                chatService.chat(request, Constants.ServiceType.SIM);
        return buildSuccessResponse(result);
    }

    // ================== PRIVATE HELPERS ==================
    private Short mapChatType(String chatType) {
        if (DataUtils.isNullOrBlank(chatType)) {
            throw new LogicException(ResponseMessage.Chat.UNSUPPORTED_CHAT_TYPE);
        }

        return switch (chatType.trim().toLowerCase()) {
            case "mobile-packages", "mobile", "mobi" ->
                    Constants.ServiceType.MOBILE_PACKAGE;
            case "ftth-packages", "ftth" ->
                    Constants.ServiceType.FTTH_PACKAGE;
            case "sims", "sim" ->
                    Constants.ServiceType.SIM;
            default ->
                    throw new LogicException(ResponseMessage.Chat.UNSUPPORTED_CHAT_TYPE);
        };
    }

    private ResponseEntity<ExecutionResult<MessageChatDTO>> buildSuccessResponse(
            MessageChatDTO data
    ) {
        ExecutionResult<MessageChatDTO> response = new ExecutionResult<>();
        response.setData(data);
        response.setResponseCode(Constants.ExecutionCode.SUCCESS);
        response.setKeyMessage(ResponseMessage.Common.SUCCESS);
        response.setDescription(
                ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS)
        );
        return ResponseEntity.ok(response);
    }
}
