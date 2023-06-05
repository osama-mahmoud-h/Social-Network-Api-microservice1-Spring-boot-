package com.example.server.payload.request.profile;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EducationRequestDto {
    private String instituteName;
    private String degree;
    private Date from ;
    private Date to ;

}
