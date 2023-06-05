package com.example.server.payload.request.profile;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProfileRequestDto {

    private String bio;

    private String aboutUser;

    private String Education;

  //  private Set<Skill> siklls;

}
