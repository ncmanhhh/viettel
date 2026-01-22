package com.datn.viettel.dto.request.search;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationSearchRequest {
    Integer page;
    Integer size;
    Integer rating;
    Short status;
    String type;
}
