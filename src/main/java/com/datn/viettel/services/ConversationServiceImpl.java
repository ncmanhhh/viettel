package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.entities.core.Conversation;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.ConversationRepository;
import com.datn.viettel.services.iservice.ConversationService;
import com.datn.viettel.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String prepareConversationId(String conversationId, Short chatType, String vectorIndex) {
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

}
