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
public class ConversationDTO {
    String id;
    String user;
    String botId;
    String botName;
    LocalDateTime startedAt;
    LocalDateTime endedAt;
    Short rating;
}
