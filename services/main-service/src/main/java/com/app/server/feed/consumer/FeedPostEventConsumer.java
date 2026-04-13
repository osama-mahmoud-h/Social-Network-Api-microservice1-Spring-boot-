package com.app.server.feed.consumer;

import com.app.server.dto.notification.post.PostEventDto;
import com.app.server.enums.PostActionType;
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
public class FeedPostEventConsumer {

    private final FeedService feedService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "post-events", groupId = "feed-service-group")
    public void onPostEvent(ConsumerRecord<String, String> record) {
        try {
            PostEventDto event = objectMapper.readValue(record.value(), PostEventDto.class);
            PostEventDto.PostData post = event.getPost();

            if (event.getActionType() == PostActionType.CREATE) {
                feedService.fanoutNewPost(
                        post.getAuthor().getUserId(),
                        event.getPostId(),
                        post.getCreatedAt() * 1000L,
                        post.getPublicity()
                );
            } else if (event.getActionType() == PostActionType.DELETE) {
                feedService.removePostFromAllFeeds(post.getAuthor().getUserId(), event.getPostId());
            }
        } catch (Exception e) {
            log.error("Failed to process post event: {}", record.value(), e);
        }
    }
}