package com.app.server.event.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Base class for all domain events
 * Represents something that happened in the domain
 */
@Getter
public abstract class DomainEvent {
    private final Instant occurredAt;
    private final Long userId; // user who triggered the event

    protected DomainEvent(Long userId) {
        this.occurredAt = Instant.now();
        this.userId = userId;
    }

    /**
     * Returns the type/name of the event for logging and routing
     */
    public abstract String getEventType();
}
