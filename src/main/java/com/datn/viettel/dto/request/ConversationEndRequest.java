package com.datn.viettel.dto.request;

import com.datn.viettel.common.ResponseMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationEndRequest {
    @NotBlank(message = ResponseMessage.Chatbot.MISSING_ID)
    String botId;
    @NotBlank(message = ResponseMessage.Conversation.MISSING_ID)
    String conversationId;
    @NotNull(message = ResponseMessage.Conversation.MISSING_RATING)
    @Min(value = 1, message = ResponseMessage.Conversation.INVALID_RATING)
    @Max(value = 5, message = ResponseMessage.Conversation.INVALID_RATING)
    Short rating;
}
