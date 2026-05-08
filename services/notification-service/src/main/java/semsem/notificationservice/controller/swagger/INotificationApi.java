package semsem.notificationservice.controller.swagger;

import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import semsem.notificationservice.dto.NotificationPageResponse;
import semsem.notificationservice.dto.NotificationResponse;
import semsem.notificationservice.enums.NotificationType;

@Tag(name = "Notification Management", description = "APIs for managing user notifications")
@SecurityRequirement(name = "jwtAuth")
public interface INotificationApi {

    @Operation(summary = "Get all notifications for current user", description = "Retrieves paginated notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<NotificationPageResponse>> getUserNotifications(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get unread notifications", description = "Retrieves paginated unread notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<NotificationPageResponse>> getUnreadNotifications(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get notifications by type", description = "Retrieves notifications of a specific type for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Page<NotificationResponse>>> getNotificationsByType(
            @Parameter(description = "Notification type") @PathVariable NotificationType type,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get unread notification count", description = "Returns the count of unread notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unread count retrieved successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Long>> getUnreadCount();

    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification marked as read",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Boolean>> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId);

    @Operation(summary = "Mark all notifications as read", description = "Marks all unread notifications as read for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All notifications marked as read",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Integer>> markAllAsRead();

    @Operation(summary = "Delete notification", description = "Deletes a specific notification for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification deleted successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Boolean>> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId);

    @Operation(summary = "Delete all notifications", description = "Deletes all notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All notifications deleted successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Boolean>> deleteAllNotifications();

    @Operation(summary = "Cleanup old notifications", description = "Admin endpoint to cleanup old read notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cleanup completed successfully",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin only",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    ResponseEntity<MyApiResponse<Integer>> cleanupOldNotifications(
            @Parameter(description = "Days old threshold") @RequestParam(defaultValue = "30") int daysOld);
}