package com.app.auth.strategy.impl;

import com.app.auth.enums.OAuthProvider;
import com.app.auth.strategy.OAuthAttributeExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class GoogleOAuthExtractor implements OAuthAttributeExtractor {

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public String extractEmail(Map<String, Object> attributes) {
        log.debug("Extracting email for Google provider");
        return (String) attributes.get("email");
    }

    @Override
    public String extractProviderId(Map<String, Object> attributes) {
        String providerId = (String) attributes.get("sub");
        log.debug("Extracting provider ID for Google: {}", providerId);
        return providerId;
    }

    @Override
    public String extractFirstName(Map<String, Object> attributes) {
        log.debug("Extracting first name for Google provider");
        return (String) attributes.get("given_name");
    }

    @Override
    public String extractLastName(Map<String, Object> attributes) {
        log.debug("Extracting last name for Google provider");
        return (String) attributes.get("family_name");
    }
}