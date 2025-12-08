package com.app.auth.strategy;

import com.app.auth.enums.OAuthProvider;

import java.util.Map;

public interface OAuthAttributeExtractor {

    OAuthProvider getProvider();

    String extractEmail(Map<String, Object> attributes);

    String extractProviderId(Map<String, Object> attributes);

    String extractFirstName(Map<String, Object> attributes);

    String extractLastName(Map<String, Object> attributes);
}