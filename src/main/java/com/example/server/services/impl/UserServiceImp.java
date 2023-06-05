package com.example.server.services.impl;

import com.example.server.exceptions.CustomErrorException;
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
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.security.jwt.JwtUtils;
import com.example.server.security.securityServices.UserDetailsImpl;
import com.example.server.services.UserService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final ProfileRepository profileRepository;
    private final AuthenticatedUser authenticatedUser;


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

    @Override
    public ResponseEntity<Object> register(@Valid @RequestBody SignupRequest signUpRequest){

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST,"Email is already in use!");
        }
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();

        roles.add(new Role(ERole.ROLE_USER));
        user.setRoles(roles);
        userRepository.save(user);

        //create new profile
        Profile profile = new Profile();
        profile.setOwner(user);
        profileRepository.save(profile);

        user.setProfile(profile);
        userRepository.save(user);

        user.setPassword("");

        return ResponseHandler
                .generateResponse("User registered successfully!", HttpStatus.CREATED,user);
    }

    @Override
    public User getUser(Long userId){
        Optional<User> user = userRepository.findUserById(userId);
        if(user.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"User not found!");
        }
        return user.get();
    }

    @Override
    public User getCurrentAuthenticatedUser(HttpServletRequest req) {
        Optional<User> user = authenticatedUser.getCurrentUser(req);
        if(user.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"User not found!");
        }
        return user.get();
    }


}
