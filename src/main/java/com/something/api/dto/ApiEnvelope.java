package com.something.api.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record ApiEnvelope<T>(
        boolean success,
        T data,
        String error,
        Meta meta
) {
    public record Meta(
            String timestamp,
            String requestId
    ) {
    }

    public static <T> ApiEnvelope<T> success(T data) {
        return new ApiEnvelope<>(true, data, null, new Meta(now(), requestId()));
    }

    public static <T> ApiEnvelope<T> error(String message) {
        return new ApiEnvelope<>(false, null, message, new Meta(now(), requestId()));
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String requestId() {
        return UUID.randomUUID().toString();
    }
}
