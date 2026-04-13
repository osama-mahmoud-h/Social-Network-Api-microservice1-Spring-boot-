package com.app.server.feed.consumer;

import com.app.server.dto.notification.friendship.FriendshipEventDto;
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
public class FeedFriendshipEventConsumer {

    private final FeedService feedService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "friendship-events", groupId = "feed-service-group")
    public void onFriendshipEvent(ConsumerRecord<String, String> record) {
        try {
            FriendshipEventDto event = objectMapper.readValue(record.value(), FriendshipEventDto.class);
            feedService.handleFriendshipChange(event.getUserId1(), event.getUserId2(), event.getActionType());
        } catch (Exception e) {
            log.error("Failed to process friendship event: {}", record.value(), e);
        }
    }
}