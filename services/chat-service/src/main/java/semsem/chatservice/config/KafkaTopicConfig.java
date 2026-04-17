package semsem.chatservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic chatMessagesTopic() {
        return TopicBuilder.name("chat-messages").partitions(2).replicas(1).build();
    }

    @Bean
    public NewTopic presenceEventsTopic() {
        return TopicBuilder.name("presence-events").partitions(2).replicas(1).build();
    }

    @Bean
    public NewTopic pushNotificationsTopic() {
        return TopicBuilder.name("push-notifications").partitions(2).replicas(1).build();
    }

    @Bean
    public NewTopic groupEventsTopic() {
        return TopicBuilder.name("group-events").partitions(2).replicas(1).build();
    }
}