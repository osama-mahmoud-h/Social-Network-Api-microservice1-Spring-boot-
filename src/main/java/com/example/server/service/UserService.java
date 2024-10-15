package com.example.server.service;

import com.example.server.dto.response.LogInResponseDto;
import com.example.server.model.*;
import com.example.server.dto.request.LoginRequestDto;
import com.example.server.dto.request.SignUpRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Service
public interface UserService {

    LogInResponseDto login(LoginRequestDto request);

    Boolean register(@Valid @RequestBody SignUpRequestDto signUpRequestDto);

    AppUser getUser(Long userId);
}
