package com.datn.viettel.dto.pojo;

import com.datn.viettel.common.Constants;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageChat implements Message {

    String role;
    String content;

    @NotNull
    @Override
    public MessageType getMessageType() {
        return switch (role) {
            case Constants.Conversation.Role.ASSISTANT -> MessageType.ASSISTANT;
            case Constants.Conversation.Role.SYSTEM -> MessageType.SYSTEM;
            case Constants.Conversation.Role.TOOL -> MessageType.TOOL;
            default -> MessageType.USER;
        };
    }

    @Override
    public String getText() {
        return content;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of();
    }

}
