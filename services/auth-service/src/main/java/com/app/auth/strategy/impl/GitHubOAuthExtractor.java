package com.app.auth.strategy.impl;

import com.app.auth.enums.OAuthProvider;
import com.app.auth.strategy.OAuthAttributeExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class GitHubOAuthExtractor implements OAuthAttributeExtractor {

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GITHUB;
    }

    @Override
    public String extractEmail(Map<String, Object> attributes) {
        log.debug("Extracting email for GitHub provider");
        return (String) attributes.get("email");
    }

    @Override
    public String extractProviderId(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));
        log.debug("Extracting provider ID for GitHub: {}", providerId);
        return providerId;
    }

    @Override
    public String extractFirstName(Map<String, Object> attributes) {
        log.debug("Extracting first name for GitHub provider");
        String name = (String) attributes.get("name");
        return name != null && name.contains(" ") ? name.split(" ")[0] : name;
    }

    @Override
    public String extractLastName(Map<String, Object> attributes) {
        log.debug("Extracting last name for GitHub provider");
        String name = (String) attributes.get("name");
        if (name != null && name.contains(" ")) {
            String[] parts = name.split(" ");
            return parts.length > 1 ? parts[parts.length - 1] : "";
        }
        return "";
    }
}