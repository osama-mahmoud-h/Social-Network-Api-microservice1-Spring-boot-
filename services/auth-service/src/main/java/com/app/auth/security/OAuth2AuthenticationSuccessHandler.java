package com.app.auth.security;

import com.app.auth.dto.request.DeviceInfoRequest;
import com.app.auth.dto.response.AuthResponse;
import com.app.auth.factory.DeviceInfoFactory;
import com.app.auth.model.User;
import com.app.auth.service.AuthService;
import com.app.auth.service.CustomOAuth2User;
import com.app.auth.service.impl.CustomOAuth2UserImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final DeviceInfoFactory deviceInfoFactory;

    public OAuth2AuthenticationSuccessHandler(@Lazy AuthService authService,
                                             DeviceInfoFactory deviceInfoFactory) {
        this.authService = authService;
        this.deviceInfoFactory = deviceInfoFactory;
    }

    @Value("${oauth2.success-redirect-url:http://localhost:3000/oauth/callback}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect.");
            return;
        }

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = ((CustomOAuth2UserImpl) oAuth2User).getUser();

        // Extract device info and generate JWT tokens
        DeviceInfoRequest deviceInfo = deviceInfoFactory.extractDeviceInfo(request);
        AuthResponse authResponse = authService.authenticateOAuth2User(user, deviceInfo);

        // Redirect to frontend with tokens as query parameters
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", authResponse.getAccessToken())
                .queryParam("userId", authResponse.getUser().getId())
                .queryParam("email", authResponse.getUser().getEmail())
                .queryParam("firstName", authResponse.getUser().getFirstName())
                .queryParam("lastName", authResponse.getUser().getLastName())
                .build().toUriString();

        log.info("OAuth2 authentication successful for user: {}", user.getEmail());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}