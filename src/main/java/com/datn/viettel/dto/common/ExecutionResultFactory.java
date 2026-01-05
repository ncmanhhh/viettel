package com.datn.viettel.dto.common;

import java.sql.Timestamp;
import java.time.Instant;

public final class ExecutionResultFactory {

    private ExecutionResultFactory() {
    }

    /* ================= SUCCESS ================= */

    public static <T> ExecutionResult<T> success(
            T data,
            String responseCode,
            String keyMessage,
            String description,
            String path
    ) {
        ExecutionResult<T> result = new ExecutionResult<>();
        result.setData(data);
        result.setResponseCode(responseCode);
        result.setKeyMessage(keyMessage);
        result.setDescription(description);
        result.setTimestamp(Timestamp.from(Instant.now()));
        result.setPath(path);
        return result;
    }

    /* ================= ERROR ================= */

    public static <T> ExecutionResult<T> error(
            String responseCode,
            String keyMessage,
            String description,
            String path
    ) {
        ExecutionResult<T> result = new ExecutionResult<>();
        result.setData(null);
        result.setResponseCode(responseCode);
        result.setKeyMessage(keyMessage);
        result.setDescription(description);
        result.setTimestamp(Timestamp.from(Instant.now()));
        result.setPath(path);
        return result;
    }
}