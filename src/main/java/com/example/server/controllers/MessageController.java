package com.example.server.controllers;

import com.example.server.payload.request.MessageRequestDto;
import com.example.server.payload.response.MessageReponseDto;
import com.example.server.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/message")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send/text")
    public ResponseEntity<?> sendTextMessage(@RequestBody MessageRequestDto messageDto) {
        return ResponseEntity.ok().body(messageService.sendTextMessage(messageDto));
    }

    @PostMapping("/image")
    public ResponseEntity<?> sendImageMessage(@RequestBody MessageRequestDto messageDto) {
        return ResponseEntity.ok().body(messageService.sendImageMessage(messageDto));
    }

    @GetMapping("/get/conversation/{senderId}/{receiverId}")
    public ResponseEntity<?> getConversation(@PathVariable("senderId") Long senderId,
                                                   @PathVariable("receiverId") Long receiverId) {
        return ResponseEntity.ok().body(messageService.getConversation(senderId, receiverId));
    }
}
