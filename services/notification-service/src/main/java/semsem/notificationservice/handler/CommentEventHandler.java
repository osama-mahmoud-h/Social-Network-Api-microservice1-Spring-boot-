package semsem.notificationservice.handler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.CommentEventDto;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.service.NotificationService;

/**
 * Handler for comment-related events
 * Processes comment creation, updates, deletions, and replies
 */
@Service
@RequiredArgsConstructor
public class CommentEventHandler {
    private static final Logger log = LoggerFactory.getLogger(CommentEventHandler.class);
    private final NotificationService notificationService;

    /**
     * Handle comment events based on action type
     *
     * @param event The comment event to process
     */
    public void handle(CommentEventDto event) {
        log.info("Processing comment event: action={}, commentId={}",
                 event.getActionType(), event.getCommentId());

        switch (event.getActionType()) {
            case "CREATE":
                handleCommentCreation(event);
                break;
            case "REPLY":
                handleCommentReply(event);
                break;
            case "UPDATE":
                handleCommentUpdate(event);
                break;
            case "DELETE":
                handleCommentDeletion(event);
                break;
            default:
                log.warn("Unknown comment action type: {}", event.getActionType());
        }
    }

    /**
     * Handle new comment creation - notify post author
     */
    private void handleCommentCreation(CommentEventDto event) {
        try {
            // Validate event data
            if (event.getComment() == null) {
                log.warn("Comment data is null for commentId={}", event.getCommentId());
                return;
            }

            Long commentAuthorId = event.getComment().getAuthor() != null
                    ? event.getComment().getAuthor().getUserId()
                    : null;
            Long postAuthorId = event.getComment().getPostAuthorId();
            Long postId = event.getComment().getPostId();

            if (commentAuthorId == null || postAuthorId == null || postId == null) {
                log.warn("Missing required data - commentAuthorId={}, postAuthorId={}, postId={}",
                         commentAuthorId, postAuthorId, postId);
                return;
            }

            // Don't notify if user comments on their own post
            if (postAuthorId.equals(commentAuthorId)) {
                log.info("User {} commented on their own post, skipping notification", commentAuthorId);
                return;
            }

            log.info("Notifying user {} about comment on their post from user {}",
                     postAuthorId, commentAuthorId);

            // Create notification event
            String commentAuthorName = event.getComment().getAuthor().getFirstName() + " "
                    + event.getComment().getAuthor().getLastName();
            String message = commentAuthorName + " commented on your post";

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .type(NotificationType.COMMENTED_YOUR_POST)
                    .message(message)
                    .senderId(commentAuthorId)
                    .receiverId(postAuthorId)
                    .build();

            // Save notification to database
            notificationService.createNotification(notificationEvent, event.getCommentId(), "COMMENT");
            log.debug("Notification persisted to database");

            // TODO: Send real-time notification via WebSocket
            // TODO: Send push notification if enabled

            log.info("Successfully notified user {} about new comment {}",
                     postAuthorId, event.getCommentId());

        } catch (Exception e) {
            log.error("Error handling comment creation for commentId={}: {}",
                     event.getCommentId(), e.getMessage(), e);
        }
    }

    /**
     * Handle comment reply - notify original comment author
     */
    private void handleCommentReply(CommentEventDto event) {
        try {
            // TODO: Get parent comment author ID and notify them
            log.info("Comment reply created: commentId={}", event.getCommentId());
            // TODO: Implement reply notification logic

        } catch (Exception e) {
            log.error("Error handling comment reply for commentId={}: {}",
                     event.getCommentId(), e.getMessage(), e);
        }
    }

    /**
     * Handle comment update
     */
    private void handleCommentUpdate(CommentEventDto event) {
        log.info("Comment updated: commentId={}", event.getCommentId());
        // TODO: Implement update logic if needed
    }

    /**
     * Handle comment deletion - cleanup related notifications
     */
    private void handleCommentDeletion(CommentEventDto event) {
        log.info("Comment deleted: commentId={}", event.getCommentId());
        // TODO: Delete or mark notifications as invalid for this comment
    }
}
