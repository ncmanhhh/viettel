//package com.datn.viettel.controllers;
//
//import com.datn.viettel.dto.ChatbotDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequestMapping("/chatbots")
//@RequiredArgsConstructor
//public class ChatbotController {
//
//    private final ChatbotService chatbotService;
//    private final AuditorAware<User> auditorAware;
//
//    @GetMapping("/v1/{botId}")
//    public ResponseEntity<ExecutionResult<ChatbotDTO>> getChatbot(
//            @PathVariable String botId,
//            HttpServletRequest http
//    ) {
//        if (DataUtils.isNullOrBlank(botId)) {
//            throw new LogicException(ResponseMessage.Chatbot.MISSING_ID);
//        }
//
//        ChatbotDTO data = chatbotService.getChatbotById(botId.trim());
//        return ResponseEntity.ok(success(data, http));
//    }
//
//    @GetMapping("/v1")
//    public ResponseEntity<ExecutionResult<Map<String, Object>>> getChatbots(
//            @RequestParam(name = "status", defaultValue = "", required = false) Short status,
//            @RequestParam(name = "name", defaultValue = "", required = false) String name,
//            @RequestParam(name = "modelId", defaultValue = "", required = false) String modelId,
//            @RequestParam(name = "page", defaultValue = Constants.Pagination.DEFAULT_PAGE_STR, required = false) Integer page,
//            @RequestParam(name = "size", defaultValue = Constants.Pagination.DEFAULT_SIZE_STR, required = false) Integer size,
//            @RequestParam(name = "fullData", required = false, defaultValue = "false") Boolean fullData,
//            @RequestParam(name = "groupFilter", defaultValue = "", required = false) String groupFilter,
//            HttpServletRequest http
//    ) {
//        Map<String, Object> data = chatbotService.getChatbots(ChatbotSearchRequest.builder()
//                .status(status)
//                .name(name == null ? "" : name.trim())
//                .modelId(modelId == null ? "" : modelId.trim())
//                .commonSearch(CommonSearch.builder()
//                        .page(page)
//                        .size(size)
//                        .fullData(fullData)
//                        .groupFilter(groupFilter == null ? "" : groupFilter.trim())
//                        .build())
//                .build());
//
//        return ResponseEntity.ok(success(data, http));
//    }
//
//    @PostMapping("/v1")
//    public ResponseEntity<ExecutionResult<Boolean>> createChatbot(
//            @Valid @RequestBody ChatbotCreateRequest req,
//            BindingResult bindingResult,
//            HttpServletRequest http
//    ) {
//        ValidatorUtils.validateBindingResult(bindingResult);
//
//        String createBy = auditorAware.getCurrentAuditor()
//                .orElseThrow(() -> new PermissionDeniedException(ResponseMessage.Authentication.PERMISSION_DENIED))
//                .getUsername();
//
//        req.setCreateBy(createBy);
//        chatbotService.createChatbot(req);
//
//        return ResponseEntity.ok(success(true, http));
//    }
//
//    @PutMapping("/v1/{botId}")
//    public ResponseEntity<ExecutionResult<Boolean>> updateChatbot(
//            @PathVariable String botId,
//            @Valid @RequestBody ChatbotUpdateRequest req,
//            BindingResult bindingResult,
//            HttpServletRequest http
//    ) {
//        ValidatorUtils.validateBindingResult(bindingResult);
//
//        if (DataUtils.isNullOrBlank(botId) || !Objects.equals(botId.trim(), req.getId())) {
//            throw new LogicException(ResponseMessage.Chatbot.MISSING_ID);
//        }
//
//        String updateBy = auditorAware.getCurrentAuditor()
//                .orElseThrow(() -> new PermissionDeniedException(ResponseMessage.Authentication.PERMISSION_DENIED))
//                .getUsername();
//
//        req.setUpdateBy(updateBy);
//        chatbotService.updateChatbot(req);
//
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
//}
