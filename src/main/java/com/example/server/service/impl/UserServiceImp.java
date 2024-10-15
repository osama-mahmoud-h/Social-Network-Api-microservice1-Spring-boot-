package com.example.server.service.impl;

import com.example.server.dto.response.LogInResponseDto;
import com.example.server.exception.CustomRuntimeException;
import com.example.server.mapper.UserMapper;
import com.example.server.model.AppUser;
import com.example.server.model.Profile;
import com.example.server.dto.request.LoginRequestDto;
import com.example.server.dto.request.SignUpRequestDto;
import com.example.server.repository.ProfileRepository;
import com.example.server.repository.AppUserRepository;
import com.example.server.security.jwt.JwtService;
import com.example.server.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private  AuthenticationManager authenticationManager;
    private AppUserRepository userRepository;
    private  PasswordEncoder encoder;
    private JwtService jwtService;
    private  ProfileRepository profileRepository;
    private final UserMapper userMapper;

    @Override
    public LogInResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid phoneNumber or password. ");
        }

        AppUser user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        String jwt =  jwtService.generateToken(user);
        return LogInResponseDto.builder()
                .token(jwt)
                .userId(user.getUserId())
                .userName(user.getUsername())
                .build();
    }

    //toDo
    //test the following login method .
    @Override
    public Boolean register(@Valid @RequestBody SignUpRequestDto signUpRequestDto){
        try {
            AppUser user = userMapper.mapSignUpRequestDtoToUser(signUpRequestDto);
            userRepository.save(user);
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

    private AppUser createUser(SignUpRequestDto signUpRequestDto) {
//        // Create new account for user
//        AppUser user = createUserAccount(signUpRequest);
//        //create new profile for user
//        Profile profile = createUserProfile(user);
//        user.setProfile(profile);
//        appUserRepository.save(user);
//        user.setPassword("");
//        return user;
        return  null;
    }

    private AppUser createUserAccount(SignUpRequestDto signUpRequestDto) {
//        AppUser user = new AppUser(signUpRequest.getUsername(),
//                signUpRequest.getEmail(),
//                encoder.encode(signUpRequest.getPassword()));
//
//        Set<UserRole> roles = new HashSet<>();
//        roles.add(new UserRole(UserRole.ROLE_USER));
//        user.setRoles(roles);
//        appUserRepository.save(user);
        return null;
    }

    private Profile createUserProfile(AppUser user) {
//        Profile profile = new Profile();
//        profile.setOwner(user);
//        profileRepository.save(profile);
//        return profile;
        return null;
    }

    @Override
    public AppUser getUser(Long userId){
//        Optional<AppUser> user = appUserRepository.findUserById(userId);
//        if(user.isEmpty()){
//            throw new CustomErrorException(HttpStatus.NOT_FOUND,"User not found!");
//        }
        return null;
    }


}
