package com.example.server.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.example.server.services.UserService;
import com.example.server.security.jwt.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.server.payload.request.LoginRequest;
import com.example.server.payload.request.SignupRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final AuthenticatedUser authenticatedUser;

  @GetMapping("/test")
  public ResponseEntity<?> test(HttpServletRequest request) {
    return ResponseEntity.ok().body(authenticatedUser.getCurrentUser(request));
  //  throw new CustomErrorException( HttpStatus.BAD_REQUEST,"Email is already in use!");
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return userService.login(loginRequest);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return userService.register(signUpRequest);
  }
}
