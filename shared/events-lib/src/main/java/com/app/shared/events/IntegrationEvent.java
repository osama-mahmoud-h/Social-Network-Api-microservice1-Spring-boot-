package com.app.shared.events;

import com.app.shared.events.type.EventType;

import java.io.Serializable;

/**
 * Marker for a message that crosses a bounded-context boundary over Kafka.
 *
 * <p>Named <em>integration</em> event rather than <em>domain</em> event on purpose: a domain
 * event is an in-process fact published inside one context (see
 * {@code com.app.server.event.app.domain.DomainEvent} in main-service), whereas the types in
 * this module are the published language between contexts. They evolve on different clocks and
 * must not be conflated.
 *
 * <p>Implementations are the contract itself. Adding an optional field is backwards compatible;
 * renaming or removing one is not.
 */
public interface IntegrationEvent extends Serializable {

    /**
     * Category of this event, for logging and routing. Not part of the wire payload —
     * implementations annotate their override with {@code @JsonIgnore}.
     */
    EventType getEventType();
}