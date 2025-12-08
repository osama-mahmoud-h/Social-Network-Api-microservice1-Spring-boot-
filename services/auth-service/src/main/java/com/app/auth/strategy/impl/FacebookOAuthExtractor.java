package com.app.auth.strategy.impl;

import com.app.auth.enums.OAuthProvider;
import com.app.auth.strategy.OAuthAttributeExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class FacebookOAuthExtractor implements OAuthAttributeExtractor {

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.FACEBOOK;
    }

    @Override
    public String extractEmail(Map<String, Object> attributes) {
        log.debug("Extracting email for Facebook provider");
        return (String) attributes.get("email");
    }

    @Override
    public String extractProviderId(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));
        log.debug("Extracting provider ID for Facebook: {}", providerId);
        return providerId;
    }

    @Override
    public String extractFirstName(Map<String, Object> attributes) {
        log.debug("Extracting first name for Facebook provider");
        String name = (String) attributes.get("name");
        return name != null ? name.split(" ")[0] : "User";
    }

    @Override
    public String extractLastName(Map<String, Object> attributes) {
        log.debug("Extracting last name for Facebook provider");
        String name = (String) attributes.get("name");
        String[] parts = name != null ? name.split(" ") : new String[]{};
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
}