package com.example.server.services.impl;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.User;
import com.example.server.payload.request.LoginRequest;
import com.example.server.payload.request.SignupRequest;
import com.example.server.repository.ProfileRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.JwtUtils;
import com.example.server.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    private UserService userService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private ProfileRepository profileRepository;
    long id ;
    @BeforeEach
    void setUp() {
        userService=new UserServiceImp(authenticationManager,userRepository,
                                        roleRepository,encoder,jwtUtils, profileRepository);
        id=1;
    }

    @Test
    void login() {
        /*
        //given
        LoginRequest loginRequest =new LoginRequest(
                "ah2093@gmail.com",
                "password"
        ) ;
        /*
        Authentication authentication =new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return ;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };
        */
        //when
        when(authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()))).thenReturn();
        //then

        */
    }

    @Test
    void registerNewUser() {
        //given
        String email = "ah2093@gmail.com";
        SignupRequest signupRequest =new SignupRequest(
                "ahmed ",
                email,
                "password1203456"
        );
        //when
        Mockito.when(userRepository.existsByEmail(email)).thenReturn(false);
        ResponseEntity<Object> expected = userService.register(signupRequest);
        //then
        verify(userRepository).existsByEmail(email);
        assertThat(expected.getStatusCode()).isEqualTo( HttpStatus.CREATED);
    }
    @Test
    void tryToRegisterExistUserThrowCustomErrorException() {
        //given
        String email = "ah2093@gmail.com";
        SignupRequest signupRequest =new SignupRequest(
                "ahmed ",
                email,
                "password1203456"
        );
        //when
        //then
        Mockito.when(userRepository.existsByEmail(email)).thenReturn(true);
        assertThatThrownBy(()->userService.register(signupRequest))
                .isInstanceOf(CustomErrorException.class)
                .hasMessageContaining("Email is already in use!");
    }


    @Test
    void checkGetUserWhenExists() {
        //given
        User user  = new User(
                "ahmed hamdi",
                "ah2093@gmail.com",
                "encryptedPass");
        Optional<User> result =Optional.of(user);
        //when
        Mockito.when(userRepository.findUserById(id)).thenReturn(result);
        Object expected = userService.getUser(id);
        //then
        verify(userRepository).findUserById(id);
        assertThat(expected).isEqualTo(user);
    }
    @Test(
    )
    void getUserWhenNotExistWillThrowException() {
        //given
        Optional<User> result =Optional.empty();
        //when
        when(userRepository.findUserById(anyLong())).thenReturn(result);
        //then
        assertThatThrownBy(()-> userService.getUser(anyLong()))
                .isInstanceOf(CustomErrorException.class)
                .hasMessageContaining("User not found!");

    }
}