package com.example.server.dto.request.comment;


import lombok.Data;

@Data
public class AddNewCommentRequestDto {
    private Long postId;
    private String content;
}