package semsem.searchservice.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import semsem.searchservice.service.KafkaConsumer;

public class KafkaConsumerImpl implements KafkaConsumer {
    //private final NotificationHandlerFactory handlerFactory;
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerImpl.class);

    @KafkaListener(topics = "notification-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, Object> record) {
        log.info("Notification event processed: {}", record.value());
    }
}
