package com.datn.viettel.controllers;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.ConversationDTO;
import com.datn.viettel.dto.MessageDTO;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.dto.request.ConversationEndRequest;
import com.datn.viettel.dto.request.search.CommonSearch;
import com.datn.viettel.dto.request.search.ConversationSearchRequest;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.MessageRepository;
import com.datn.viettel.services.iservice.ConversationService;
import com.datn.viettel.utils.DataUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/history")
    public ResponseEntity<ExecutionResult<Map<String, Object>>> getConversationHistory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Short status,
            @RequestParam(required = false) String type,
            HttpServletRequest http
    ) {
        if (page < 0 || size <= 0) {
            throw new LogicException(ResponseMessage.Common.INVALID_REQUEST);
        }

        ConversationSearchRequest search = ConversationSearchRequest.builder()
                .page(page)
                .size(size)
                .rating(rating)
                .status(status)
                .type(type)
                .build();

        Map<String, Object> data = conversationService.getConversations(search);

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

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<ExecutionResult<List<MessageDTO>>> getConversationHistoryDetail(
            @PathVariable String conversationId,
            HttpServletRequest http
    ) {
        if (!DataUtils.isValidUUID(conversationId)) {
            throw new LogicException(ResponseMessage.Conversation.MISSING_ID);
        }

        List<MessageDTO> data = conversationService.getConversationDetail(conversationId);

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

    @PostMapping("/rating")
    public ResponseEntity<ExecutionResult<Boolean>> createRating(
            @Valid @RequestBody ConversationEndRequest request,
            HttpServletRequest http
    ) {
        if (!DataUtils.isValidUUID(request.getConversationId())) {
            throw new LogicException(ResponseMessage.Conversation.MISSING_ID);
        }

        conversationService.createRating(request);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        true,
                        Constants.ExecutionCode.SUCCESS,
                        ResponseMessage.Common.SUCCESS,
                        ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                        http.getRequestURI()
                )
        );
    }
}