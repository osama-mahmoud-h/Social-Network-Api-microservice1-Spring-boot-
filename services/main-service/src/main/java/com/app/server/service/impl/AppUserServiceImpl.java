package com.app.server.service.impl;

import com.app.server.repository.AppUserRepository;
import com.app.server.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository userRepository;
    @Override
    public UserDetailsService userDetailsService() {
        return email ->  userRepository.findUserByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
