package com.app.auth.service.impl;

import com.app.auth.model.dto.request.RegisterRequest;
import com.app.auth.model.dto.response.RegistrationResponse;
import com.app.auth.event.UserRegisteredEvent;
import com.app.auth.exception.UserAlreadyExistsException;
import com.app.auth.mapper.AuthMapper;
import com.app.auth.model.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public RegistrationResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        User user = authMapper.mapToUser(request, passwordEncoder.encode(request.getPassword()));
        user.prepareForRegistrationVerification();
        userRepository.save(user);

        // Core business logic ends here. We publish the event!
        // Listeners will handle OTP, Welcome Emails, Analytics, etc., asynchronously.
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getEmail()));

        return RegistrationResponse.builder()
                .message("Registration successful. Please check your email for the verification code.")
                .email(request.getEmail())
                // otpExpiresAt is intentionally omitted since OTP generation is now 100% async
                .build();
    }
}
