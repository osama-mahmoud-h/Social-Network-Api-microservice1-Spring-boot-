package com.app.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
//import org.springframework.kafka.annotation.KafkaListener;

@Component
@RequiredArgsConstructor
public class KafkaListenersImp {
    private final ObjectMapper objectMapper;

//    @KafkaListener(
//            topics = "elasticsearch_insert",
//            groupId = "mygroup1",
//            containerFactory = "postListenerFactory"
//    )
//    void listen(String message) throws JsonProcessingException {
//        try {
//            PostResponceDto postResponceDto =
//                    objectMapper.readValue(message, PostResponceDto.class);
//            System.out.println("postResponceDto from Kafka:  " + postResponceDto);
//        }catch (Exception ex){
//            System.out.println("exception: " + ex.getMessage());
//        }
//    }
}
