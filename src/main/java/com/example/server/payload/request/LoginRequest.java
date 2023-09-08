package com.example.server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class LoginRequest {
	@NotBlank(message = "please provide username")
  private String email;

	@NotBlank(message = "please provide password")
	private String password;

}
