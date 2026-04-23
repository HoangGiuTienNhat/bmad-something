package com.something.api.dto;

public record ApiEnvelope<T>(
        boolean success,
        T data,
        Meta meta
) {
    public record Meta(
            String timestamp,
            String requestId
    ) {
    }
}
