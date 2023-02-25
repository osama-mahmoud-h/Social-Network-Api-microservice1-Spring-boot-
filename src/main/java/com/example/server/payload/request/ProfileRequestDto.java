package com.example.server.payload.request;


import com.example.server.models.Skill;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
public class ProfileRequestDto {

    private String bio;

    private String aboutUser;

    private String Education;

  //  private Set<Skill> siklls;

}
