package com.app.server.service.impl;

import com.app.server.dto.response.LogInResponseDto;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.UserMapper;
import com.app.server.model.AppUser;
import com.app.server.model.Profile;
import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.SignUpRequestDto;
import com.app.server.repository.ProfileRepository;
import com.app.server.repository.AppUserRepository;
import com.app.server.security.jwt.JwtService;
import com.app.server.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);
    private  AuthenticationManager authenticationManager;
    private AppUserRepository userRepository;
    private JwtService jwtService;
    private final UserMapper userMapper;
    private final ProfileRepository profileRepository;

    @Override
    public LogInResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid phoneNumber or password. ");
        }

        AppUser user = this.getUserByEmail(request.getEmail());
        String jwt =  jwtService.generateToken(user);

        return userMapper.mapToLogInResponseDto(user, jwt);
    }

    @Override
    public Boolean register(@Valid @RequestBody SignUpRequestDto signUpRequestDto){
        try {
            AppUser user = userMapper.mapSignUpRequestDtoToUser(signUpRequestDto);
            userRepository.save(user);
            createUserProfile(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("phoneNumber/email already exists", e);
            String rootMsg = getRootCause(e).getMessage();
            if (rootMsg != null && rootMsg.contains("email")) {
                throw new CustomRuntimeException("email already exists", HttpStatus.CONFLICT);
            }
            throw new CustomRuntimeException("phoneNumber already exists", HttpStatus.CONFLICT);
        }
    }

    private void createUserProfile(AppUser user) {
        Profile profile =  Profile.builder()
                .user(user)
                .build();
        profileRepository.save(profile);
    }

    private AppUser getAppUserById(Long userId){
       return userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new CustomRuntimeException("User not found", HttpStatus.NOT_FOUND));
    }

    private AppUser getUserByEmail(String email){
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException("User not found", HttpStatus.NOT_FOUND));
    }


}
