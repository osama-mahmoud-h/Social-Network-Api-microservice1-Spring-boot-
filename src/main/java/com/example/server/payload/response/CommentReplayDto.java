package com.example.server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReplayDto {
    private Long id;
    private String text;
    private UserResponceDto author;
    private Timestamp timestamp;
}
