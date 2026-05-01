package semsem.chatservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageKafkaProducer {

    private static final String TOPIC = "chat-messages";

    private final KafkaTemplate<String, ChatMessageEvent> kafkaTemplate;

    public void publish(ChatMessageEvent event) {
        CompletableFuture<SendResult<String, ChatMessageEvent>> future =
                kafkaTemplate.send(TOPIC, event.getConversationId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish messageId={} to Kafka: {}", event.getMessageId(), ex.getMessage());
            } else {
                log.debug("Published messageId={} to partition={} offset={}",
                        event.getMessageId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}