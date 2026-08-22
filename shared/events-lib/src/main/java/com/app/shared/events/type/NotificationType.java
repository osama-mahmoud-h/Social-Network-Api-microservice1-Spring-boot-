package com.app.shared.events.type;

/**
 * Notification vocabulary shared by main-service (producer) and notification-service (consumer).
 *
 * <p>Note: notification-service also persists this enum as a string column. Adding a constant is
 * safe; renaming or removing one requires a data migration there.
 */
public enum NotificationType {
    REQUEST_FRIENDSHIP,
    ACCEPT_FRIENDSHIP,

    COMMENTED_YOUR_POST,

    REACTED_TO_YOUR_POST,
    REACTED_TO_YOUR_COMMENT,
    REPLIED_TO_YOUR_COMMENT,

    POSTED_NEW_CONTENT
}