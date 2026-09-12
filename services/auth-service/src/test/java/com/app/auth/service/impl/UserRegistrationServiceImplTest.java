package com.app.auth.service.impl;

import com.app.auth.model.dto.request.RegisterRequest;
import com.app.auth.model.dto.response.RegistrationResponse;
import com.app.auth.event.UserRegisteredEvent;
import com.app.auth.exception.UserAlreadyExistsException;
import com.app.auth.mapper.AuthMapper;
import com.app.auth.model.entity.User;
import com.app.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering existing user")
    void shouldThrowExceptionWhenRegisteringExistingUser() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("duplicate@example.com")
                .password("password123")
                .build();
                
        // The service uses existsByEmail now!
        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> userRegistrationService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        then(userRepository).should(never()).save(any());
        then(passwordEncoder).shouldHaveNoInteractions();
        then(eventPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Should save user and publish event when registering new user")
    void shouldSaveUserAndPublishEventWhenRegisteringNewUser() {
        // Given
        String email = "newuser@example.com";
        String password = "securePassword";
        String encodedPassword = "encoded_secured_password";

        RegisterRequest request = RegisterRequest.builder()
                .email(email)
                .password(password)
                .build();

        User mappedUser = User.builder()
                .email(email)
                // We MUST not set enabled/emailVerified to true here.
                // The MAPPER should just build a standard unconfigured user.
                // It is the SERVICE's job to call prepareForRegistrationVerification() 
                // which sets those flags before saving!
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(authMapper.mapToUser(request, encodedPassword)).willReturn(mappedUser);
        given(userRepository.save(any(User.class))).willReturn(mappedUser);

        // When
        RegistrationResponse response = userRegistrationService.registerUser(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getMessage()).contains("Registration successful");
        assertThat(response.getOtpExpiresAt()).isNull();

        // But we DO verify that the service successfully called prepareForRegistrationVerification() 
        // to set those flags before it passed it to the save method!
        then(userRepository).should().save(argThat(user -> 
                user.isEnabled() == false && user.isEmailVerified() == false
        ));
        
        then(eventPublisher).should().publishEvent(argThat((UserRegisteredEvent event) -> 
            event.email().equals(email)
        ));
    }
}
