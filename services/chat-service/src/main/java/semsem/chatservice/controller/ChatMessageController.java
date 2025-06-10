package semsem.chatservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import semsem.chatservice.dto.request.NewPrivateChatMessageRequestDto;
import semsem.chatservice.dto.request.NewPublicChatMessageRequestDto;
import semsem.chatservice.dto.response.ChatMessageResponseDto;
import semsem.chatservice.service.ChatMessageService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;

   @MessageMapping("/chat")
    public void saveNewMessage(@Payload NewPublicChatMessageRequestDto requestDto) {
        ChatMessageResponseDto savedMessage = chatMessageService.saveMessage(requestDto);
        simpMessagingTemplate.convertAndSendToUser(savedMessage.getReceiverId(),"/queue/messages", savedMessage);
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getConversations(
            @PathVariable("senderId") String senderId,
            @PathVariable("receiverId") String receiverId
    ) {
        return ResponseEntity.ok(chatMessageService.getConversations(senderId, receiverId));
    }

    @MessageMapping("/message.sendPrivateMessage")
    public void handlePrivateMessage(@Payload NewPrivateChatMessageRequestDto msg) {
        simpMessagingTemplate.convertAndSendToUser(msg.getReceiverId(), "/private", msg);
    }
}
