package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.MessageDTO;
import com.datn.viettel.dto.request.ConversationCreateRequest;
import com.datn.viettel.dto.request.ConversationEndRequest;
import com.datn.viettel.dto.request.search.CommonSearch;

import java.util.List;
import java.util.Map;

public interface ConversationService {
    String prepareConversationId(String conversationId, Short chatType, String vectorIndex);

    String createConversation(
            ConversationCreateRequest request,
            Short chatType,
            String vectorIndex
    );

    void createOrUpdateCustomer(
            String conversationId,
            String customerPhone
    );

    Map<String, Object> getConversations(CommonSearch search);

    List<MessageDTO> getConversationDetail(String conversationId);

    void createRating(ConversationEndRequest request);

}
