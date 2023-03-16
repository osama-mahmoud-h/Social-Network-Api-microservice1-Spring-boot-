package com.example.server.services;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.*;
import com.example.server.payload.request.LoginRequest;
import com.example.server.payload.request.SignupRequest;
import com.example.server.payload.response.JwtResponse;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.repository.ProfileRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.JwtUtils;
import com.example.server.security.securityServices.UserDetailsImpl;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public interface UserService {

    ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest);

    ResponseEntity<Object> register(@Valid @RequestBody SignupRequest signUpRequest);

    User getUser(Long userId);
}
