package semsem.notificationservice.controller;

import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import semsem.notificationservice.controller.swagger.INotificationApi;
import semsem.notificationservice.dto.NotificationPageResponse;
import semsem.notificationservice.dto.NotificationResponse;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController implements INotificationApi {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<NotificationPageResponse>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching notifications for user: {}, page: {}, size: {}", currentUserId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        NotificationPageResponse response = notificationService.getUserNotifications(currentUserId, pageable);
        return ResponseEntity.ok(MyApiResponse.success("Notifications retrieved successfully", response));
    }

    @Override
    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<NotificationPageResponse>> getUnreadNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching unread notifications for user: {}", currentUserId);
        Pageable pageable = PageRequest.of(page, size);
        NotificationPageResponse response = notificationService.getUnreadNotifications(currentUserId, pageable);
        return ResponseEntity.ok(MyApiResponse.success("Unread notifications retrieved successfully", response));
    }

    @Override
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Page<NotificationResponse>>> getNotificationsByType(
            @PathVariable NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching notifications by type: {} for user: {}", type, currentUserId);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getNotificationsByType(currentUserId, type, pageable);
        return ResponseEntity.ok(MyApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @Override
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Long>> getUnreadCount() {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Long count = notificationService.getUnreadCount(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("Unread count retrieved successfully", count));
    }

    @Override
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> markAsRead(
            @PathVariable Long notificationId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Marking notification {} as read for user {}", notificationId, currentUserId);
        notificationService.markAsRead(notificationId, currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("Notification marked as read", true));
    }

    @Override
    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Integer>> markAllAsRead() {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Marking all notifications as read for user {}", currentUserId);
        int updatedCount = notificationService.markAllAsRead(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("All notifications marked as read", updatedCount));
    }

    @Override
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> deleteNotification(
            @PathVariable Long notificationId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Deleting notification {} for user {}", notificationId, currentUserId);
        notificationService.deleteNotification(notificationId, currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("Notification deleted successfully", true));
    }

    @Override
    @DeleteMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> deleteAllNotifications() {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Deleting all notifications for user {}", currentUserId);
        notificationService.deleteAllNotifications(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("All notifications deleted successfully", true));
    }

    @Override
    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Integer>> cleanupOldNotifications(
            @RequestParam(defaultValue = "30") int daysOld) {

        log.info("Cleaning up notifications older than {} days", daysOld);
        int deletedCount = notificationService.cleanupOldNotifications(daysOld);
        return ResponseEntity.ok(MyApiResponse.success("Cleanup completed", deletedCount));
    }
}