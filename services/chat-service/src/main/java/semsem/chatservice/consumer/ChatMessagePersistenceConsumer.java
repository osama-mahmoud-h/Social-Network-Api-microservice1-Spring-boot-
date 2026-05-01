package semsem.chatservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import semsem.chatservice.event.ChatMessageEvent;
import semsem.chatservice.mapper.ChatMessageEventMapper;
import semsem.chatservice.repository.ConversationMessageRepository;
import semsem.chatservice.repository.RedisChatMessageRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessagePersistenceConsumer {

    private final ConversationMessageRepository conversationMessageRepository;
    private final RedisChatMessageRepository redisChatMessageRepository;
    private final ChatMessageEventMapper mapper;

    @KafkaListener(
            topics = "chat-messages",
            groupId = "chat-persistence",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void persist(ChatMessageEvent event, Acknowledgment ack) {
        log.debug("Persisting messageId={} for conversation={}", event.getMessageId(), event.getConversationId());
        try {
            conversationMessageRepository.save(mapper.toConversationMessage(event));

            updateRecentMessageCache(event);

            // Commit offset only after successful write to Cassandra.
            // If Cassandra crashes before this line, offset is NOT committed
            // and Kafka will redeliver to the next consumer instance.
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to persist messageId={} — offset NOT committed, will retry: {}",
                    event.getMessageId(), e.getMessage(), e);
        }
    }

    private void updateRecentMessageCache(ChatMessageEvent event) {
        try {
            redisChatMessageRepository.saveMessage(mapper.toChatMessage(event));
        } catch (Exception e) {
            // Redis failure is non-critical: Cassandra is the source of truth
            log.warn("Redis cache update skipped for messageId={}: {}", event.getMessageId(), e.getMessage());
        }
    }
}