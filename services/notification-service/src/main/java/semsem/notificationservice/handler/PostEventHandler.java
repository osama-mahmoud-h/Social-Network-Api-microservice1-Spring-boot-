package semsem.notificationservice.handler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import semsem.notificationservice.client.MainServiceClient;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.dto.PostEventDto;
import semsem.notificationservice.enums.NotificationType;
import semsem.notificationservice.repository.NotificationRepository;
import semsem.notificationservice.service.NotificationService;

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
    private final NotificationService notificationService;
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
            // Validate event data
            if (event.getPost() == null || event.getPost().getAuthor() == null) {
                log.warn("Post or author data is null for postId={}", event.getPostId());
                return;
            }

            Long authorId = event.getPost().getAuthor().getUserId();
            String authorName = event.getPost().getAuthor().getFirstName() + " "
                    + event.getPost().getAuthor().getLastName();

            // Fetch friends to notify
            List<Long> friendIds = mainServiceClient.getFriendIds(authorId);

            if (friendIds.isEmpty()) {
                log.debug("User {} has no friends to notify about post {}", authorId, event.getPostId());
                return;
            }

            log.info("Notifying {} friends about new post {} from user {}",
                     friendIds.size(), event.getPostId(), authorId);

            //TODO: handle batching if friendIds is large.
            //TODO: consider async processing for scalability.
            //TODO: either fanout early or late depending on use case.
            // Fan-out: Create notification for each friend
            friendIds.forEach(friendId -> {
                try {
                    String message = authorName + " posted new content";

                    NotificationEvent notificationEvent = NotificationEvent.builder()
                            .type(NotificationType.POSTED_NEW_CONTENT)
                            .message(message)
                            .senderId(authorId)
                            .receiverId(friendId)
                            .build();

                    // Save notification to database
                    notificationService.createNotification(notificationEvent, event.getPostId(), "POST");
                    log.debug("Notification persisted for user {} about post {}", friendId, event.getPostId());

                    // TODO: Send real-time notification via WebSocket
                    // TODO: Send push notification if enabled

                } catch (Exception e) {
                    log.error("Error creating notification for user {} about post {}: {}",
                            friendId, event.getPostId(), e.getMessage());
                }
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