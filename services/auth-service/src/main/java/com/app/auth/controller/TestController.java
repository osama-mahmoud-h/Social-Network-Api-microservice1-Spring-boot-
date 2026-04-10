package com.app.auth.controller;

import com.app.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Profile("dev")
public class TestController {

    private final UserRepository userRepository;

    @PostMapping("/users/{userId}/verify-email")
    public ResponseEntity<String> verifyEmail(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setEmailVerified(true);
                    userRepository.save(user);
                    return ResponseEntity.ok("User " + userId + " marked as email verified");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}