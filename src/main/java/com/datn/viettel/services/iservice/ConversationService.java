package com.datn.viettel.services.iservice;

public interface ConversationService {
    String prepareConversationId(String conversationId, Short chatType, String vectorIndex);
}
