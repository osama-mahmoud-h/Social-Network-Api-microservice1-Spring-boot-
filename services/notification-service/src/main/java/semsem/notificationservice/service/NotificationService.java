package semsem.notificationservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.dto.NotificationPageResponse;
import semsem.notificationservice.dto.NotificationResponse;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.model.Notification;

import java.time.LocalDateTime;

public interface NotificationService {

    /**
     * Create and save a notification from an event
     */
    Notification createNotification(NotificationEvent event);

    /**
     * Create and save a notification with reference
     */
    Notification createNotification(NotificationEvent event, Long referenceId, String referenceType);

    /**
     * Get all notifications for a user with pagination
     */
    NotificationPageResponse getUserNotifications(Long userId, Pageable pageable);

    /**
     * Get unread notifications for a user
     */
    NotificationPageResponse getUnreadNotifications(Long userId, Pageable pageable);

    /**
     * Get notifications by type for a user
     */
    Page<NotificationResponse> getNotificationsByType(Long userId, NotificationType type, Pageable pageable);

    /**
     * Mark a notification as read
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * Mark all notifications as read for a user
     */
    int markAllAsRead(Long userId);

    /**
     * Get unread notification count for a user
     */
    Long getUnreadCount(Long userId);

    /**
     * Delete a notification
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * Delete all notifications for a user
     */
    void deleteAllNotifications(Long userId);

    /**
     * Cleanup old read notifications
     */
    int cleanupOldNotifications(int daysOld);
}