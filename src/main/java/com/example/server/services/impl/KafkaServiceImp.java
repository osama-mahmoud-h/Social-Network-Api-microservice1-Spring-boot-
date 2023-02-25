package com.example.server.services.impl;


import com.example.server.models.Post;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.UserResponceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaServiceImp {
    private final Logger LOG = LoggerFactory.getLogger(KafkaServiceImp.class);
  //  private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.name}")
    private String TOPICNAME  ;

    public void publishMessage(PostResponceDto postDto)throws JsonProcessingException {

        String postString = objectMapper.writeValueAsString(postDto);
        System.out.println("postString published to kafka: " + postString);
      ///  kafkaTemplate.send(TOPICNAME, postString);
    }
    public void publishList(List<Object> userList) {
        LOG.info("Sending UserList Json Serializer : {}", userList);
        for (Object user : userList) {
        ///    kafkaTemplate.send(TOPICNAME, user.toString());
        }
    }
}
