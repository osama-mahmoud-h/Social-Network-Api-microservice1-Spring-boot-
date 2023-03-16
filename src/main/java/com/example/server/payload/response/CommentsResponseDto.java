package com.example.server.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentsResponseDto {
    private Long id;
    private String text;
    private UserResponceDto author;
}
