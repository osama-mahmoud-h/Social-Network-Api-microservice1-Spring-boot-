package com.example.server.dto.response;


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
    private AppUserResponseDto author;
}
