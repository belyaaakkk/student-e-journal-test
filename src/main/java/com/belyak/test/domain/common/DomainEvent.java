package com.belyak.test.domain.common;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = this.getClass().getSimpleName();
    }
}