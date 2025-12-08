package com.app.auth.service.impl;

import com.app.auth.enums.OAuthProvider;
import com.app.auth.enums.UserRole;
import com.app.auth.mapper.AuthMapper;
import com.app.auth.model.User;
import com.app.auth.publisher.UserEventPublisher;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.CustomOAuth2UserService;
import com.app.auth.strategy.OAuthAttributeExtractor;
import com.app.auth.strategy.OAuthExtractorRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements CustomOAuth2UserService {
    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final AuthMapper authMapper;
    private final OAuthExtractorRegistry extractorRegistry;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("Processing OAuth2 user for registrationId: {}", registrationId);

        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());
        log.info("Identified OAuth2 provider: {}", provider);

        // Get the appropriate extractor for this provider
        OAuthAttributeExtractor extractor = extractorRegistry.getExtractor(provider);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = extractor.extractEmail(attributes);
        String providerId = extractor.extractProviderId(attributes);

        log.info("OAuth2 login attempt - Provider: {}, Email: {}", provider, email);

        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, provider, providerId))
                .orElseGet(() -> createNewUser(email, attributes, provider, providerId, extractor));

        return CustomOAuth2UserImpl.builder()
                .oAuth2User(oAuth2User)
                .user(user)
                .build();
    }

    private User updateExistingUser(User user, OAuthProvider provider, String providerId) {
        // Link OAuth provider if not already linked
        if (user.getOauthProvider() == null || user.getOauthProvider() == OAuthProvider.LOCAL) {
            user.setOauthProvider(provider);
            user.setOauthProviderId(providerId);
            user.setEmailVerified(true);
            return userRepository.save(user);
        }
        return user;
    }

    private User createNewUser(String email, Map<String, Object> attributes,
                               OAuthProvider provider, String providerId,
                               OAuthAttributeExtractor extractor) {
        String firstName = extractor.extractFirstName(attributes);
        String lastName = extractor.extractLastName(attributes);

        User newUser = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .oauthProvider(provider)
                .oauthProviderId(providerId)
                .emailVerified(true)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .password("")
                .roles(Set.of(UserRole.USER))
                .build();

        log.info("Creating new OAuth2 user: {}", newUser.getEmail());
        User createdUser = userRepository.save(newUser);

        userEventPublisher.publishUserCreated(authMapper.mapToUserCreatedEvent(createdUser)).exceptionally(ex -> {
            log.error("Failed to publish UserCreatedEvent async for userId: {}", createdUser.getUserId(), ex);
            return null;
        });
        log.info("Published UserCreatedEvent Async for userId: {}", createdUser.getUserId());
        return createdUser;
    }
}
