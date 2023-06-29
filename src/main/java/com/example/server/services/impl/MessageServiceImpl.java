package com.example.server.services.impl;

import com.example.server.models.Message;
import com.example.server.payload.request.MessageRequestDto;
import com.example.server.payload.response.MessageReponseDto;
import com.example.server.repository.MessageRepository;
import com.example.server.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public boolean sendTextMessage(MessageRequestDto messageDto){
        Message message = new Message();

        message.setSenderId(messageDto.getSenderId());
        message.setReceiverId(messageDto.getReceiverId());
        message.setSenderName(messageDto.getSenderName());
        message.setText(messageDto.getText());

        messageRepository.save(message);

        return true;
    }
    @Override
    public boolean sendImageMessage(MessageRequestDto messageDto){
        Message message = new Message();

        message.setSenderId(messageDto.getSenderId());
        message.setReceiverId(messageDto.getReceiverId());
        message.setSenderName(messageDto.getSenderName());
        message.setImage(messageDto.getImage());

        messageRepository.save(message);

        return true;
    }
    @Override

    public List<MessageReponseDto> getConversation (Long senderId,Long receiverId){
        List<MessageReponseDto> conversation1 = messageRepository.getMessageByReceiverIdAndSenderId(senderId,receiverId)
                        .stream()
                        .map(msg->mapMessageToMessageDto(msg))
                        .collect(Collectors.toList());

        List<MessageReponseDto> conversation2 = messageRepository.getMessageBySenderIdAndReceiverId(senderId,receiverId)
                .stream()
                .map(msg->mapMessageToMessageDto(msg))
                .collect(Collectors.toList());
        List<MessageReponseDto> conversation = new ArrayList<>();
        conversation.addAll(conversation1);
        conversation.addAll(conversation2);
        return conversation;
    }

    private MessageReponseDto mapMessageToMessageDto(Message message){
        MessageReponseDto msgDto = new MessageReponseDto();
        msgDto.setId(message.getId());
        msgDto.setTimestamp(message.getTimestamp());
        msgDto.setText(message.getText());
        msgDto.setSenderId(message.getSenderId());
        msgDto.setReceiverId(message.getReceiverId());
        msgDto.setSenderName(message.getSenderName());
        return msgDto;
    }
}
