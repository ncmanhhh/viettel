package com.datn.viettel.configs.ai;

import com.datn.viettel.common.Constants;
import com.datn.viettel.dto.pojo.MessageChat;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.ai.chat.messages.*;

import java.util.Map;

@Data
public class MessageWrapper {

    private String messageType;   // USER / ASSISTANT / SYSTEM
    private String content;
    private Map<String, Object> metadata; // vẫn lưu, nhưng tạm thời không set lại vào Message

    @JsonCreator
    public MessageWrapper(
            @JsonProperty("messageType") String messageType,
            @JsonProperty("content") String content,
            @JsonProperty("metadata") Map<String, Object> metadata
    ) {
        this.messageType = messageType;
        this.content = content;
        this.metadata = metadata;
    }

    public static MessageWrapper fromMessage(Message message) {
        // Lưu theo MessageType để ổn định
        return new MessageWrapper(
                message.getMessageType().name(),
                message.getText(),
                message.getMetadata()
        );
    }

    public Message toMessage() {
        // Dùng constructor public có sẵn trong version bạn đang dùng
        return switch (messageType) {
            case "ASSISTANT" -> new AssistantMessage(content); // metadata bỏ qua
            case "SYSTEM" -> new SystemMessage(content);       // metadata bỏ qua
            default -> new UserMessage(content);               // metadata bỏ qua
        };
    }
}