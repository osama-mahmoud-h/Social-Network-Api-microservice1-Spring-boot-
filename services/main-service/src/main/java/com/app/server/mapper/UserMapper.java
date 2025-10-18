package com.app.server.mapper;

import com.app.server.dto.request.SignUpRequestDto;
import com.app.server.dto.response.AppUserResponseDto;
import com.app.server.dto.response.LogInResponseDto;
import com.app.server.dto.response.user.AuthorResponseDto;
import com.app.server.model.UserProfile;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserProfile mapSignUpRequestDtoToUser(SignUpRequestDto requestDto) {
        return UserProfile.builder()
                .email(requestDto.getEmail())
                //.password(requestDto.getPassword())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
               // .password(passwordEncoder.encode(requestDto.getPassword()))
                .createdAt(Instant.now())
                //.userRole(UserRole.USER)
                .build();
    }

    public AuthorResponseDto mapToAuthorResponseDto(UserProfile appUser){
        return AuthorResponseDto.builder()
                .userId(appUser.getUserId())
                .email(appUser.getEmail())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .profilePictureUrl(null)
                .build();
    }

    public AppUserResponseDto mapToAppUserResponseDto(UserProfile appUser) {
        return AppUserResponseDto.builder()
                .userId(appUser.getUserId())
                .email(appUser.getEmail())
                .username(appUser.getFirstName() + " " + appUser.getLastName())
                .image_url(null)
                .build();
    }

    public LogInResponseDto mapToLogInResponseDto(UserProfile appUser, String jwt) {
        return LogInResponseDto.builder()
                .token(jwt)
                .userId(appUser.getUserId())
                .userName(appUser.getFirstName() + " " + appUser.getLastName())
                .build();
    }
}
