package com.example.server.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class MessageRequestDto {
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String text;
    private String image;
}
