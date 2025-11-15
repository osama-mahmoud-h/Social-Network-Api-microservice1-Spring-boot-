package semsem.notificationservice.controller;

import com.app.shared.security.dto.MyApiResponse;
import com.app.shared.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import semsem.notificationservice.dto.NotificationPageResponse;
import semsem.notificationservice.dto.NotificationResponse;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
@SecurityRequirement(name = "jwtAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications for current user", description = "Retrieves paginated notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<NotificationPageResponse>> getUserNotifications(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching notifications for user: {}, page: {}, size: {}", currentUserId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        NotificationPageResponse response = notificationService.getUserNotifications(currentUserId, pageable);
        return ResponseEntity.ok(MyApiResponse.success("Notifications retrieved successfully", response));
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Retrieves paginated unread notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<NotificationPageResponse>> getUnreadNotifications(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching unread notifications for user: {}", currentUserId);
        Pageable pageable = PageRequest.of(page, size);
        NotificationPageResponse response = notificationService.getUnreadNotifications(currentUserId, pageable);
        return ResponseEntity.ok(MyApiResponse.success("Unread notifications retrieved successfully", response));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get notifications by type", description = "Retrieves notifications of a specific type for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Page<NotificationResponse>>> getNotificationsByType(
            @Parameter(description = "Notification type") @PathVariable NotificationType type,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching notifications by type: {} for user: {}", type, currentUserId);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getNotificationsByType(currentUserId, type, pageable);
        return ResponseEntity.ok(MyApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", description = "Returns the count of unread notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unread count retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Long>> getUnreadCount() {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Long count = notificationService.getUnreadCount(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("Unread count retrieved successfully", count));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification marked as read",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Marking notification {} as read for user {}", notificationId, currentUserId);
        notificationService.markAsRead(notificationId, currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("Notification marked as read", true));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Marks all unread notifications as read for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All notifications marked as read",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Integer>> markAllAsRead() {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Marking all notifications as read for user {}", currentUserId);
        int updatedCount = notificationService.markAllAsRead(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("All notifications marked as read", updatedCount));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Deletes a specific notification for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification deleted successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Deleting notification {} for user {}", notificationId, currentUserId);
        notificationService.deleteNotification(notificationId, currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("Notification deleted successfully", true));
    }

    @DeleteMapping
    @Operation(summary = "Delete all notifications", description = "Deletes all notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All notifications deleted successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Boolean>> deleteAllNotifications() {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Deleting all notifications for user {}", currentUserId);
        notificationService.deleteAllNotifications(currentUserId);
        return ResponseEntity.ok(MyApiResponse.success("All notifications deleted successfully", true));
    }

    @DeleteMapping("/cleanup")
    @Operation(summary = "Cleanup old notifications", description = "Admin endpoint to cleanup old read notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cleanup completed successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin only",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<Integer>> cleanupOldNotifications(
            @Parameter(description = "Days old threshold") @RequestParam(defaultValue = "30") int daysOld) {

        log.info("Cleaning up notifications older than {} days", daysOld);
        int deletedCount = notificationService.cleanupOldNotifications(daysOld);
        return ResponseEntity.ok(MyApiResponse.success("Cleanup completed", deletedCount));
    }
}