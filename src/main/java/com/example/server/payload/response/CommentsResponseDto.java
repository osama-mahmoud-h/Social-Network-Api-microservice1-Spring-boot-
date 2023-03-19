package com.example.server.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Map;

@Data
@ToString
//@NoArgsConstructor
@AllArgsConstructor
public class CommentsResponseDto {
    private Long id;
    private String text;
    private UserResponceDto author;
    private byte myFeed;
    private Map<Byte, Long> feeds;
    private Timestamp timestamp;

    public CommentsResponseDto(){
        myFeed = 0;

    }

}
