package com.example.server.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Setter
@Getter
public class MessageReponseDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String text;
    private String image;
    private String status;
    private Timestamp timestamp;
}
