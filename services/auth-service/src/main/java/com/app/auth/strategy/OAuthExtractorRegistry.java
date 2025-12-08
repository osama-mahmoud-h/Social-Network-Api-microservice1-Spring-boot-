package com.app.auth.strategy;

import com.app.auth.enums.OAuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@Slf4j
public class OAuthExtractorRegistry {

    private final Map<OAuthProvider, OAuthAttributeExtractor> extractorMap;


    public OAuthExtractorRegistry(List<OAuthAttributeExtractor> extractors) {
        this.extractorMap = extractors.stream()
                .collect(Collectors.toMap(
                        OAuthAttributeExtractor::getProvider,
                        Function.identity()
                ));

        log.info("Registered {} OAuth attribute extractors: {}",
                extractorMap.size(),
                extractorMap.keySet());
    }

    public OAuthAttributeExtractor getExtractor(OAuthProvider provider) {
        OAuthAttributeExtractor extractor = extractorMap.get(provider);
        if (extractor == null) {
            log.error("No OAuth attribute extractor found for provider: {}", provider);
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + provider);
        }
        log.debug("Retrieved extractor for provider: {}", provider);
        return extractor;
    }
}