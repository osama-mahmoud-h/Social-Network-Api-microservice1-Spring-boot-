package semsem.notificationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a specific receiver with pagination
     */
    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    /**
     * Find unread notifications for a specific receiver
     */
    Page<Notification> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    /**
     * Find read notifications for a specific receiver
     */
    Page<Notification> findByReceiverIdAndIsReadTrueOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    /**
     * Find notifications by type for a receiver
     */
    Page<Notification> findByReceiverIdAndTypeOrderByCreatedAtDesc(Long receiverId, NotificationType type, Pageable pageable);

    /**
     * Count unread notifications for a receiver
     */
    Long countByReceiverIdAndIsReadFalse(Long receiverId);

    /**
     * Find notifications created after a specific date
     */
    List<Notification> findByReceiverIdAndCreatedAtAfter(Long receiverId, LocalDateTime after);

    /**
     * Mark all unread notifications as read for a specific receiver
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.receiverId = :receiverId AND n.isRead = false")
    int markAllAsRead(@Param("receiverId") Long receiverId, @Param("readAt") LocalDateTime readAt);

    /**
     * Mark specific notification as read
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :notificationId AND n.receiverId = :receiverId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("receiverId") Long receiverId, @Param("readAt") LocalDateTime readAt);

    /**
     * Delete all notifications for a receiver
     */
    void deleteByReceiverId(Long receiverId);

    /**
     * Delete old read notifications (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.readAt < :before")
    int deleteOldReadNotifications(@Param("before") LocalDateTime before);

    /**
     * Find notifications by reference (e.g., all notifications related to a specific post)
     */
    List<Notification> findByReferenceIdAndReferenceType(Long referenceId, String referenceType);
}