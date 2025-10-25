package semsem.notificationservice.handler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import semsem.notificationservice.client.MainServiceClient;
import semsem.notificationservice.dto.PostEventDto;

import java.util.List;

/**
 * Handler for post-related events
 * Processes post creation, updates, and deletions
 */
@Service
@RequiredArgsConstructor
public class PostEventHandler {
    private static final Logger log = LoggerFactory.getLogger(PostEventHandler.class);
    private final MainServiceClient mainServiceClient;

    /**
     * Handle post events based on action type
     *
     * @param event The post event to process
     */
    public void handle(PostEventDto event) {
        log.info("Processing post event: action={}, postId={}", event.getActionType(), event.getPostId());

        switch (event.getActionType()) {
            case "CREATE":
                handlePostCreation(event);
                break;
            case "UPDATE":
                handlePostUpdate(event);
                break;
            case "DELETE":
                handlePostDeletion(event);
                break;
            default:
                log.warn("Unknown post action type: {}", event.getActionType());
        }
    }

    /**
     * Handle new post creation - notify all friends
     */
    private void handlePostCreation(PostEventDto event) {
        try {
            Long authorId = event.getPost().getAuthor().getUserId();
            List<Long> friendIds = mainServiceClient.getFriendIds(authorId);

            log.info("Notifying {} friends about new post {} from user {}",
                     friendIds.size(), event.getPostId(), authorId);

            // Fan-out: Create notification for each friend
            friendIds.forEach(friendId -> {
                // TODO: Create and save notification entity
                // TODO: Send real-time notification via WebSocket
                // TODO: Send push notification if enabled
                log.debug("Sending notification to user {} about post {} from user {}",
                         friendId, event.getPostId(), authorId);
            });

            log.info("Successfully notified {} friends about new post {}", friendIds.size(), event.getPostId());

        } catch (Exception e) {
            log.error("Error handling post creation for postId={}: {}", event.getPostId(), e.getMessage(), e);
        }
    }

    /**
     * Handle post update - optionally notify friends
     */
    private void handlePostUpdate(PostEventDto event) {
        log.info("Post updated: postId={}", event.getPostId());
        // TODO: Implement update notification logic if needed
    }

    /**
     * Handle post deletion - cleanup related notifications
     */
    private void handlePostDeletion(PostEventDto event) {
        log.info("Post deleted: postId={}", event.getPostId());
        // TODO: Delete or mark notifications as invalid for this post
    }
}