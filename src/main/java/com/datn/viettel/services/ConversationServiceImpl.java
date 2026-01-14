package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.MessageDTO;
import com.datn.viettel.dto.request.ConversationCreateRequest;
import com.datn.viettel.dto.request.ConversationEndRequest;
import com.datn.viettel.dto.request.search.CommonSearch;
import com.datn.viettel.entities.core.Conversation;
import com.datn.viettel.entities.core.ConversationMessage;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.ConversationRepository;
import com.datn.viettel.repositories.core.MessageRepository;
import com.datn.viettel.services.iservice.ConversationService;
import com.datn.viettel.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String prepareConversationId(
                String conversationId,
                Short chatType,
                String vectorIndex) {
        if (DataUtils.isNullOrBlank(conversationId) || !DataUtils.isValidUUID(conversationId)) {
            UUID uuid = conversationRepository.save(Conversation.builder()
                    .startedAt(LocalDateTime.now())
                    .type(vectorIndex)
                    .status(Constants.Status.ACTIVE)
                    .build()).getId();
            String newConversationId = uuid.toString();
            log.info("Created new conversation: {}", newConversationId);
            return newConversationId;
        } else {
            UUID conversationUuid = DataUtils.parseStringToUUID(conversationId);
            if (conversationUuid == null) {
                throw new LogicException(ResponseMessage.Conversation.NOT_FOUND);
            }
            conversationRepository.findById(conversationUuid)
                    .orElseThrow(() -> new LogicException(ResponseMessage.Conversation.NOT_FOUND));
            return conversationId;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createConversation(
            ConversationCreateRequest request,
            Short chatType,
            String vectorIndex
    ) {
        // 1. Tạo hoặc validate conversationId
        String conversationId = prepareConversationId(
                request.getConversationId(),
                chatType,
                vectorIndex
        );

        // 2. Gán customerPhone nếu có
        if (!DataUtils.isNullOrBlank(request.getCustomerPhone())) {
            createOrUpdateCustomer(conversationId, request.getCustomerPhone());
        }

        return conversationId;
    }

    @Override
    @Transactional
    public void createOrUpdateCustomer(
            String conversationId,
            String customerPhone
    ) {
        if (!DataUtils.isValidUUID(conversationId)
                || DataUtils.isNullOrBlank(customerPhone)) {
            return;
        }

        Conversation conversation = conversationRepository.findById(
                UUID.fromString(conversationId)
        ).orElseThrow(() ->
                new LogicException(ResponseMessage.Conversation.NOT_FOUND)
        );

        if (DataUtils.isNullOrBlank(conversation.getCustomer())) {
            conversation.setCustomer(customerPhone.trim());
            conversationRepository.save(conversation);
        }
    }

    @Override
    public Map<String, Object> getConversations(CommonSearch search) {

        int page = Optional.ofNullable(search.getPage()).orElse(0);
        int size = Optional.ofNullable(search.getSize()).orElse(20);

        Pageable pageable = PageRequest.of(page, size, Sort.by("startedAt").descending());

        Page<Conversation> pageData;

        if (DataUtils.isNullOrBlank(search.getGroupFilter())) {
            pageData = conversationRepository.findByStatus(
                    Constants.Status.ACTIVE,
                    pageable
            );
        } else {
            pageData = conversationRepository.findByStatusAndType(
                    Constants.Status.ACTIVE,
                    search.getGroupFilter(),
                    pageable
            );
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", pageData.getContent());
        result.put("total", pageData.getTotalElements());
        result.put("page", page);
        result.put("size", size);

        return result;
    }

    @Override
    public List<MessageDTO> getConversationDetail(String conversationId) {

        UUID uuid = DataUtils.parseStringToUUID(conversationId);
        if (uuid == null) {
            throw new LogicException(ResponseMessage.Conversation.MISSING_ID);
        }

        List<ConversationMessage> messages =
                messageRepository.findByConversationIdOrderBySentAtAsc(uuid);

        return messages.stream()
                .flatMap(m -> Stream.of(
                        MessageDTO.builder()
                                .id(m.getId().toString())
                                .senderType(Constants.Conversation.Role.USER)
                                .content(m.getQuestion())
                                .sentAt(m.getSentAt())
                                .token(m.getToken())
                                .build(),
                        MessageDTO.builder()
                                .id(m.getId().toString())
                                .senderType(Constants.Conversation.Role.ASSISTANT)
                                .content(m.getAnswer())
                                .sentAt(m.getSentAt())
                                .build()
                ))
                .toList();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRating(ConversationEndRequest request) {

        UUID conversationId = DataUtils.parseStringToUUID(request.getConversationId());
        if (conversationId == null) {
            throw new LogicException(ResponseMessage.Conversation.MISSING_ID);
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new LogicException(ResponseMessage.Conversation.NOT_FOUND));

        conversation.setRating(request.getRating());
        conversation.setEndedAt(LocalDateTime.now());
        conversation.setStatus(Constants.Status.INACTIVE);

        conversationRepository.save(conversation);
    }
}
