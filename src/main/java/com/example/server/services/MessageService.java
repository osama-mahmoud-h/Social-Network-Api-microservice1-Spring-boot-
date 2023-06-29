package com.example.server.services;

import com.example.server.payload.request.MessageRequestDto;
import com.example.server.payload.response.MessageReponseDto;
import com.example.server.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    boolean sendTextMessage(MessageRequestDto messageDto);

    boolean sendImageMessage(MessageRequestDto messageDto);

    List<MessageReponseDto> getConversation(Long senderId, Long receiverId);
}
