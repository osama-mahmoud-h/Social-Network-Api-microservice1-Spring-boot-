package com.app.server.security.jwt;


import com.app.server.model.AppUser;

public interface JwtService {
    String extractUserName(String token);
    //String extractUserPhoneNumber(String token);

    String extractUserEmail(String token);

    //String extractUserIdentifier(String token);

    String extractUserId(String token);

    String generateToken(AppUser appUser);

    boolean isTokenValid(String token, AppUser appUser);
}
