package semsem.notificationservice.dto;

import java.io.Serializable;

/**
 * Base interface for all domain events in the notification service.
 * Ensures type safety and common event metadata.
 */
public interface DomainEvent extends Serializable {
    /**
     * Get the type/category of the event
     */
    String getEventType();

    /**
     * Get the action performed (CREATE, UPDATE, DELETE, etc.)
     */
    String getActionType();
}