package semsem.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.dto.NotificationPageResponse;
import semsem.notificationservice.dto.NotificationResponse;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.mapper.NotificationMapper;
import semsem.notificationservice.model.Notification;
import semsem.notificationservice.repository.NotificationRepository;
import semsem.notificationservice.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Notification createNotification(NotificationEvent event) {
        log.info("Creating notification for receiver: {}, type: {}", event.getReceiverId(), event.getType());
        Notification notification = NotificationMapper.toEntity(event);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Notification createNotification(NotificationEvent event, Long referenceId, String referenceType) {
        log.info("Creating notification for receiver: {}, type: {}, reference: {}:{}",
                event.getReceiverId(), event.getType(), referenceType, referenceId);
        Notification notification = NotificationMapper.toEntity(event, referenceId, referenceType);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPageResponse getUserNotifications(Long userId, Pageable pageable) {
        log.debug("Fetching notifications for user: {}", userId);
        Page<Notification> notificationPage = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
        System.out.println("notification Page : "+notificationPage);
        Long unreadCount = notificationRepository.countByReceiverIdAndIsReadFalse(userId);

        List<NotificationResponse> notifications = notificationPage.getContent()
                .stream()
                .map(NotificationMapper::toResponse)
                .collect(Collectors.toList());

        return NotificationPageResponse.builder()
                .notifications(notifications)
                .currentPage(notificationPage.getNumber())
                .totalPages(notificationPage.getTotalPages())
                .totalElements(notificationPage.getTotalElements())
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPageResponse getUnreadNotifications(Long userId, Pageable pageable) {
        log.debug("Fetching unread notifications for user: {}", userId);
        Page<Notification> notificationPage = notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
        Long unreadCount = notificationRepository.countByReceiverIdAndIsReadFalse(userId);

        List<NotificationResponse> notifications = notificationPage.getContent()
                .stream()
                .map(NotificationMapper::toResponse)
                .collect(Collectors.toList());

        return NotificationPageResponse.builder()
                .notifications(notifications)
                .currentPage(notificationPage.getNumber())
                .totalPages(notificationPage.getTotalPages())
                .totalElements(notificationPage.getTotalElements())
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByType(Long userId, NotificationType type, Pageable pageable) {
        log.debug("Fetching notifications by type: {} for user: {}", type, userId);
        return notificationRepository.findByReceiverIdAndTypeOrderByCreatedAtDesc(userId, type, pageable)
                .map(NotificationMapper::toResponse);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.info("Marking notification {} as read for user: {}", notificationId, userId);
        int updated = notificationRepository.markAsRead(notificationId, userId, LocalDateTime.now());
        if (updated == 0) {
            log.warn("Notification {} not found or does not belong to user {}", notificationId, userId);
        }
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        return notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("Deleting notification {} for user: {}", notificationId, userId);
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getReceiverId().equals(userId)) {
                notificationRepository.delete(notification);
            } else {
                log.warn("User {} attempted to delete notification {} that belongs to user {}",
                        userId, notificationId, notification.getReceiverId());
            }
        });
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        log.info("Deleting all notifications for user: {}", userId);
        notificationRepository.deleteByReceiverId(userId);
    }

    @Override
    @Transactional
    public int cleanupOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        log.info("Cleaning up read notifications older than {}", cutoffDate);
        return notificationRepository.deleteOldReadNotifications(cutoffDate);
    }
}