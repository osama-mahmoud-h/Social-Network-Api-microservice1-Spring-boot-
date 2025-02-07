package com.app.server.service;

import com.app.server.dto.response.LogInResponseDto;
import com.app.server.model.*;
import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.SignUpRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Service
public interface UserService {

    LogInResponseDto login(LoginRequestDto request);

    Boolean register(@Valid @RequestBody SignUpRequestDto signUpRequestDto);

    AppUser getUser(Long userId);
}
