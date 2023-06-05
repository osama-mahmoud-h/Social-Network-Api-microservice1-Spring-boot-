package com.example.server.services;

import com.example.server.models.*;
import com.example.server.payload.request.LoginRequest;
import com.example.server.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Service
public interface UserService {

    ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest);

    ResponseEntity<Object> register(@Valid @RequestBody SignupRequest signUpRequest);

    User getUser(Long userId);

    User getCurrentAuthenticatedUser(HttpServletRequest req);
}
