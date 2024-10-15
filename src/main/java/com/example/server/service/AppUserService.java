package com.example.server.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService {
    UserDetailsService userDetailsService();
}
