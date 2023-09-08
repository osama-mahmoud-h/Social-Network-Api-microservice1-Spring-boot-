package com.example.server.services.impl;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.ERole;
import com.example.server.models.Profile;
import com.example.server.models.Role;
import com.example.server.models.User;
import com.example.server.payload.request.LoginRequest;
import com.example.server.payload.request.SignupRequest;
import com.example.server.payload.response.JwtResponse;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.repository.ProfileRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.JwtUtils;
import com.example.server.security.securityServices.UserDetailsImpl;
import com.example.server.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private  AuthenticationManager authenticationManager;
    private  UserRepository userRepository;
    private  RoleRepository roleRepository;
    private  PasswordEncoder encoder;
    private  JwtUtils jwtUtils;
    private  ProfileRepository profileRepository;


    @Override
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
    //toDo
    //test the following login method .
    @Override
    public ResponseEntity<Object> register(@Valid @RequestBody SignupRequest signUpRequest){

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST,"Email is already in use!");
        }
        User user = createUser(signUpRequest);
        return ResponseHandler
                .generateResponse("User registered successfully!", HttpStatus.CREATED,user);
    }

    private User createUser(SignupRequest signUpRequest) {
        // Create new account for user
        User user = createUserAccount(signUpRequest);
        //create new profile for user
        Profile profile = createUserProfile(user);
        user.setProfile(profile);
        userRepository.save(user);
        user.setPassword("");
        return user;
    }

    private User createUserAccount(SignupRequest signUpRequest) {
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        user.setRoles(roles);
        userRepository.save(user);
        return user;
    }

    private Profile createUserProfile(User user) {
        Profile profile = new Profile();
        profile.setOwner(user);
        profileRepository.save(profile);
        return profile;
    }

    @Override
    public User getUser(Long userId){
        Optional<User> user = userRepository.findUserById(userId);
        if(user.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"User not found!");
        }
        return user.get();
    }


}
