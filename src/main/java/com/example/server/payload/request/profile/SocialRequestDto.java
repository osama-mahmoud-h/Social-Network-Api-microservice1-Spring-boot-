package com.example.server.payload.request.profile;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialRequestDto {
    private String name;
    private String url;
}
