package com.datn.viettel.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutionResult<T> {
    T data;
    String responseCode;
    String keyMessage;
    String description;
    Timestamp timestamp;
    String path;
}
