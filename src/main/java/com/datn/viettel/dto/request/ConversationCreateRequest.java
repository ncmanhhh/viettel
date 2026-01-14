package com.datn.viettel.dto.request;

import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.pojo.MessageChat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationCreateRequest {
    String conversationId;
    String customerPhone;
    @NotBlank(message = ResponseMessage.Conversation.MISSING_CONTENT)
    @Length(max = 500, message = ResponseMessage.Conversation.INVALID_CONTENT_LENGTH)
    String prompt;
    @JsonIgnore
    LocalDateTime requestTime;
    @JsonIgnore
    String createBy;
    List<MessageChat> conversations;
}
