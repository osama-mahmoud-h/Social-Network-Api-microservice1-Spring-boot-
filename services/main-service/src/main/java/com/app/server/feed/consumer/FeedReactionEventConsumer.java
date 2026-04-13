package com.app.server.feed.consumer;

import com.app.server.dto.notification.reaction.ReactionEventDto;
import com.app.server.enums.ReactionActionType;
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
public class FeedReactionEventConsumer {

    private final FeedService feedService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "like-events", groupId = "feed-service-group")
    public void onReactionEvent(ConsumerRecord<String, String> record) {
        try {
            ReactionEventDto event = objectMapper.readValue(record.value(), ReactionEventDto.class);

            if (event.getActionType() == ReactionActionType.ADDED) {
                feedService.fanoutFriendActivity(
                        event.getReactorUserId(),
                        event.getPostId(),
                        System.currentTimeMillis()
                );
            }
        } catch (Exception e) {
            log.error("Failed to process reaction event: {}", record.value(), e);
        }
    }
}