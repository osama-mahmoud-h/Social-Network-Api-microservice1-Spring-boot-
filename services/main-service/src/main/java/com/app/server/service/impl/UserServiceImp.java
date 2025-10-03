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
import com.app.server.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);
    private AppUserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileRepository profileRepository;

    @Override
    public LogInResponseDto login(LoginRequestDto request) {
        // Authentication is now handled by auth-service
        // This method should be removed or call auth-service
        throw new UnsupportedOperationException("Authentication is handled by auth-service");
    }

    @Override
    public Boolean register(@Valid SignUpRequestDto signUpRequestDto){
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
