package com.datn.viettel.controllers;

import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.ConversationDTO;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.MessageRepository;
import com.datn.viettel.services.iservice.ConversationService;
import com.datn.viettel.utils.DataUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

//    private final ConversationService conversationService;
//    private final MessageRepository messageRepository; // để lấy message detail từ DB (conversation_messages)
//
//    @GetMapping("/wv/v1/history")
//    public ResponseEntity<ExecutionResult<Map<String, Object>>> getConversationHistory(
//            @RequestParam(name = "botId", defaultValue = "") String botId,
//            @RequestParam(name = "page", defaultValue = "0") Integer page,
//            @RequestParam(name = "size", defaultValue = "20") Integer size,
//            HttpServletRequest http
//    ) {
//        if (DataUtils.isNullOrBlank(botId)) {
//            throw new LogicException(ResponseMessage.Chatbot.MISSING_ID);
//        }
//
//        Map<String, Object> data = conversationService.getConversations(botId.trim(), page, size);
//        return ResponseEntity.ok(success(data, http));
//    }
//
//    @GetMapping("/wv/v1/history/{conversationId}")
//    public ResponseEntity<ExecutionResult<List<ConversationDTO>>> getConversationHistoryDetail(
//            @PathVariable String conversationId,
//            HttpServletRequest http
//    ) {
//        if (DataUtils.isNullOrBlank(conversationId) || !DataUtils.isValidUUID(conversationId)) {
//            throw new LogicException(ResponseMessage.Conversation.MISSING_ID);
//        }
//
//        UUID convId = UUID.fromString(conversationId.trim());
//        List<ConversationDTO> data = messageRepository.findByConversationIdOrderBySentAtAsc(convId)
//                .stream()
//                .map(m -> ConversationDTO.builder()
//                        .id(m.getId())
//                        .conversationId(m.getConversationId())
//                        .question(m.getQuestion())
//                        .answer(m.getAnswer())
//                        .sentAt(m.getSentAt())
//                        .token(m.getToken())
//                        .build())
//                .toList();
//
//        return ResponseEntity.ok(success(data, http));
//    }
//
//    @PostMapping("/wv/v1/rating")
//    public ResponseEntity<ExecutionResult<Boolean>> createRating(
//            @Valid @RequestBody ConversationEndRequest req,
//            HttpServletRequest http
//    ) {
//        // Validate UUID
//        if (!DataUtils.isValidUUID(req.getConversationId())) {
//            throw new LogicException(ResponseMessage.Conversation.MISSING_ID);
//        }
//        conversationService.createRating(req); // set endedAt + rating + status
//        return ResponseEntity.ok(success(true, http));
//    }
//
//    private <T> ExecutionResult<T> success(T data, HttpServletRequest http) {
//        String key = ResponseMessage.Common.SUCCESS;
//        return ExecutionResult.<T>builder()
//                .data(data)
//                .responseCode(Constants.ExecutionCode.SUCCESS)
//                .keyMessage(key)
//                .description(ResourceMessageConfig.getResourceMessage(key))
//                .timestamp(new Timestamp(System.currentTimeMillis()))
//                .path(http.getRequestURI())
//                .build();
//    }
}
