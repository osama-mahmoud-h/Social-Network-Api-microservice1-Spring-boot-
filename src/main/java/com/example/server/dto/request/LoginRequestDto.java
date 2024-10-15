package com.example.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class LoginRequestDto {
	@NotBlank(message = "please provide username")
    private String email;

	@NotBlank(message = "please provide password")
	private String password;

}
