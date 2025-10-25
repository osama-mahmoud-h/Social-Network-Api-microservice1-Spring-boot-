package semsem.notificationservice.handler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.CommentEventDto;

/**
 * Handler for comment-related events
 * Processes comment creation, updates, deletions, and replies
 */
@Service
@RequiredArgsConstructor
public class CommentEventHandler {
    private static final Logger log = LoggerFactory.getLogger(CommentEventHandler.class);

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
            Long postAuthorId = event.getComment().getPost().getAuthor().getUserId();
            Long commentAuthorId = event.getComment().getAuthor().getUserId();

            // Don't notify if user comments on their own post
            if (postAuthorId.equals(commentAuthorId)) {
                log.debug("User {} commented on their own post, skipping notification", commentAuthorId);
                return;
            }

            log.info("Notifying user {} about comment on their post from user {}",
                     postAuthorId, commentAuthorId);

            // TODO: Create and save notification entity
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
