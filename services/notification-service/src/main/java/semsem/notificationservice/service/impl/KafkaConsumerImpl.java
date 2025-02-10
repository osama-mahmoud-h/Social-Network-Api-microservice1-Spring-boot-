package semsem.notificationservice.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import semsem.notificationservice.dto.NotificationEvent;
import semsem.notificationservice.service.KafkaConsumer;

@Service
public class KafkaConsumerImpl implements KafkaConsumer {
    @KafkaListener(topics = "notification-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, NotificationEvent> record) {
        System.out.println("Received event: " + record);
    }
}
