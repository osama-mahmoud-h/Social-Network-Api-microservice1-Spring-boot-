//package com.app.auth.publisher;
//
//import com.app.auth.event.UserCreatedEvent;
//import com.app.auth.event.UserUpdatedEvent;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class UserEventPublisher {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//    private final ObjectMapper objectMapper;
//
//    private static final String USER_EVENTS_TOPIC = "user-events";
//
//    public void publishUserCreated(UserCreatedEvent event) {
//        try {
//            String eventJson = objectMapper.writeValueAsString(event);
//            kafkaTemplate.send(USER_EVENTS_TOPIC, event.getUserId().toString(), eventJson);
//            log.info("Published UserCreatedEvent for userId: {}", event.getUserId());
//        } catch (JsonProcessingException e) {
//            log.error("Error serializing UserCreatedEvent: {}", e.getMessage(), e);
//        }
//    }
//
//    public void publishUserUpdated(UserUpdatedEvent event) {
//        try {
//            String eventJson = objectMapper.writeValueAsString(event);
//            kafkaTemplate.send(USER_EVENTS_TOPIC, event.getUserId().toString(), eventJson);
//            log.info("Published UserUpdatedEvent for userId: {}", event.getUserId());
//        } catch (JsonProcessingException e) {
//            log.error("Error serializing UserUpdatedEvent: {}", e.getMessage(), e);
//        }
//    }
//}