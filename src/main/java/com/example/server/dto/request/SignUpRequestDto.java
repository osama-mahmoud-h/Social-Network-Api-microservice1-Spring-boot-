package com.example.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.*;

@Data //getters and setters
@AllArgsConstructor
public class SignUpRequestDto {
  @NotBlank(message = "please provide firstName")
  @Size(min = 3, max = 20)
  private String firstName;

  @NotBlank(message = "please provide lastName")
  @Size(min = 3, max = 20)
  private String lastName;

  @NotBlank(message = "please provide email")
  @Size(max = 50)
  @Email(message = "please provide valid email")
  private String email;

  @NotBlank
  @NotBlank(message = "please provide password")
  @Size(min = 6, max = 40)
  private String password;

}
