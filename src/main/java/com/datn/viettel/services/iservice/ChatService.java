package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.MessageChatDTO;
import com.datn.viettel.dto.request.ChatRequest;

public interface ChatService {

    MessageChatDTO chat(ChatRequest request, Short chatType);

}
