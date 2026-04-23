package com.something.domain.entity;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEntity {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
