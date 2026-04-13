package com.app.server.feed.consumer;

import com.app.server.dto.notification.comment.CommentEventDto;
import com.app.server.enums.CommentActionType;
import com.app.server.feed.FeedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedCommentEventConsumer {

    private final FeedService feedService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "comment-events", groupId = "feed-service-group")
    public void onCommentEvent(ConsumerRecord<String, String> record) {
        try {
            CommentEventDto event = objectMapper.readValue(record.value(), CommentEventDto.class);

            if (event.getActionType() == CommentActionType.CREATE) {
                feedService.fanoutFriendActivity(
                        event.getComment().getAuthor().getUserId(),
                        event.getComment().getPostId(),
                        System.currentTimeMillis()
                );
            }
        } catch (Exception e) {
            log.error("Failed to process comment event: {}", record.value(), e);
        }
    }
}