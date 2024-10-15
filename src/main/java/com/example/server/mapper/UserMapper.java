package com.example.server.mapper;

import com.example.server.dto.request.SignUpRequestDto;
import com.example.server.enums.UserRole;
import com.example.server.model.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public AppUser mapSignUpRequestDtoToUser(SignUpRequestDto requestDto) {
        return AppUser.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .createdAt(Instant.now())
                .userRole(UserRole.USER)
                .build();
    }
}
