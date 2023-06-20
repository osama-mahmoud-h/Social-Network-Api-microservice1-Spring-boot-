package com.example.server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostResponceDto {
    private Long id;
    private Date timestamp;
    private String text;
    private String[] images_url;
    private String vedio_url;
    private String file_url;
    private Long comments_count;
    private UserResponceDto author;
    private byte myFeed;
    private Map<Byte,Long> feeds;

}


