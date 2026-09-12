package com.app.auth.service.impl;

import com.app.auth.model.dto.request.ForgotPasswordRequest;
import com.app.auth.model.dto.request.SendOtpRequest;
import com.app.auth.model.dto.response.ForgotPasswordResponse;
import com.app.auth.model.dto.response.OtpResponse;
import com.app.auth.model.enums.OtpStatus;
import com.app.auth.model.enums.OtpType;
import com.app.auth.exception.UserNotFoundException;
import com.app.auth.model.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.OtpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should throw Exception when user not found")
    void shouldThrowExceptionWhenUserNotFoundForForgotPassword() {
        // Given
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("missing@example.com")
                .build();
                
        // The service uses findByEmail
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> authService.forgotPassword(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

        then(otpService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Should send OTP and return Response when user exists")
    void shouldSendOtpAndReturnResponseWhenUserExists() {
        // Given
        String email = "found@example.com";
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email(email)
                .build();

        User existingUser = User.builder()
                .email(email)
                .build();

        Instant otpExpiry = Instant.now().plusSeconds(300);
        OtpResponse otpResponse = OtpResponse.builder()
                .status(OtpStatus.PENDING)
                .message("OTP sent")
                .expiresAt(otpExpiry)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));
        given(otpService.sendOtp(any(SendOtpRequest.class))).willReturn(otpResponse);

        // When
        ForgotPasswordResponse response = authService.forgotPassword(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getMessage()).contains("OTP has been sent");
        assertThat(response.getOtpExpiresAt()).isEqualTo(otpExpiry);

        then(otpService).should().sendOtp(argThat((SendOtpRequest otpReq) -> 
            otpReq.getEmail().equals(email) && otpReq.getType() == OtpType.PASSWORD_RESET
        ));
    }
}
