package com.app.server.mapper;

import com.app.server.dto.request.SignUpRequestDto;
import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.dto.response.LogInResponseDto;
import com.app.server.dto.response.user.AuthorResponseDto;
import com.app.server.enums.UserRole;
import com.app.server.model.AppUser;
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

    public AuthorResponseDto mapToAuthorResponseDto(AppUser appUser){
        return AuthorResponseDto.builder()
                .userId(appUser.getUserId())
                .email(appUser.getEmail())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .profilePictureUrl(null)
                .build();
    }

    public AppUserResponseDto mapToAppUserResponseDto(AppUser appUser) {
        return AppUserResponseDto.builder()
                .userId(appUser.getUserId())
                .email(appUser.getEmail())
                .username(appUser.getFirstName() + " " + appUser.getLastName())
                .image_url(null)
                .build();
    }

    public LogInResponseDto mapToLogInResponseDto(AppUser appUser, String jwt) {
        return LogInResponseDto.builder()
                .token(jwt)
                .userId(appUser.getUserId())
                .userName(appUser.getFirstName() + " " + appUser.getLastName())
                .build();
    }
}
