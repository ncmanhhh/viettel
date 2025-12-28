package com.datn.viettel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageChatDTO {
    String conversationId;
    String role;
    String content;
    String language;
    LocalDateTime requestTime;
    LocalDateTime responseTime;
    Object moreInfo;
}

