package com.datn.viettel.dto.request;

import com.datn.viettel.common.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRequest {
    String conversationId;
    String customerPhone;
    @NotBlank(message = ResponseMessage.Conversation.MISSING_CONTENT)
    @Length(max = 500, message = ResponseMessage.Conversation.INVALID_CONTENT_LENGTH)
    String prompt;
    @JsonIgnore
    String advancedPrompt;
    @JsonIgnore
    LocalDateTime requestTime = LocalDateTime.now();
    @JsonIgnore
    String createBy;

}
