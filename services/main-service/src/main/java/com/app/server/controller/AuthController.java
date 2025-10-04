package com.app.server.controller;

import com.app.server.dto.response.LogInResponseDto;
import com.app.server.dto.response.MyApiResponse;
import com.app.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.SignUpRequestDto;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {

  private final UserService userService;

  @Operation(summary = "User login", description = "Authenticate user and return JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully authenticated",
          content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials",
          content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
  })
  @PostMapping("/login")
  public ResponseEntity<MyApiResponse<LogInResponseDto>> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
    return ResponseEntity.ok().body(MyApiResponse.success(userService.login(loginRequestDto), "User logged in successfully"));
  }

  @Operation(summary = "User registration", description = "Register a new user account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered",
          content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or user already exists",
          content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
  })
  @PostMapping("/signup")
  public ResponseEntity<MyApiResponse<Boolean>> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
    return ResponseEntity.ok().body(MyApiResponse.success(userService.register(signUpRequestDto), "User registered successfully"));
  }
}
