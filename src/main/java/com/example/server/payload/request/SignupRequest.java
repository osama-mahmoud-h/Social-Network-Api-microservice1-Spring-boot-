package com.example.server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

import javax.validation.constraints.*;

@Data //getters and setters
@AllArgsConstructor
public class SignupRequest {
  @NotBlank(message = "please provide username")
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank(message = "please provide email")
  @Size(max = 50)
  @Email(message = "please provide valid email")
  private String email;

  @NotBlank
  @NotBlank(message = "please provide password")
  @Size(min = 6, max = 40)
  private String password;

}
