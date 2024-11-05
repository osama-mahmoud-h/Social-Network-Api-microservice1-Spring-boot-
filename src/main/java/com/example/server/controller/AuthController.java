package com.example.server.controller;

import com.example.server.dto.response.LogInResponseDto;
import com.example.server.dto.response.MyApiResponse;
import com.example.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.server.dto.request.LoginRequestDto;
import com.example.server.dto.request.SignUpRequestDto;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<MyApiResponse<LogInResponseDto>> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
    return ResponseEntity.ok().body(MyApiResponse.success(userService.login(loginRequestDto), "User logged in successfully"));
  }

  @PostMapping("/signup")
  public ResponseEntity<MyApiResponse<Boolean>> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
    return ResponseEntity.ok().body(MyApiResponse.success(userService.register(signUpRequestDto), "User registered successfully"));
  }
}
