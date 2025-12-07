package com.app.auth.service.impl;

import com.app.auth.enums.OAuthProvider;
import com.app.auth.enums.UserRole;
import com.app.auth.mapper.AuthMapper;
import com.app.auth.model.User;
import com.app.auth.publisher.UserEventPublisher;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.CustomOAuth2UserService;
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

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = extractEmail(attributes, provider);
        String providerId = extractProviderId(attributes, provider);

        log.info("OAuth2 login attempt - Provider: {}, Email: {}", provider, email);

        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, provider, providerId))
                .orElseGet(() -> createNewUser(email, attributes, provider, providerId));

        return CustomOAuth2UserImpl.builder()
                .oAuth2User(oAuth2User)
                .user(user)
                .build();
    }

    private String extractEmail(Map<String, Object> attributes, OAuthProvider provider) {
        log.info("Extracting email for provider: {}", provider);
        return switch (provider) {
            case GOOGLE, GITHUB, FACEBOOK -> (String) attributes.get("email");
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
        };
    }

    private String extractProviderId(Map<String, Object> attributes, OAuthProvider provider) {
        log.info("Extracting provider ID for provider: {}, with id: {}", provider,
                provider.name().equals(OAuthProvider.GOOGLE.name()) ? attributes.get("sub") : attributes.get("id"));
        return switch (provider) {
            case GOOGLE -> (String) attributes.get("sub");
            case FACEBOOK, GITHUB -> String.valueOf(attributes.get("id"));
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
        };
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
                               OAuthProvider provider, String providerId) {
        String firstName = extractFirstName(attributes, provider);
        String lastName = extractLastName(attributes, provider);

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

    private String extractFirstName(Map<String, Object> attributes, OAuthProvider provider) {
        log.info("Extracting first name for provider: {}", provider);
        return switch (provider) {
            case GOOGLE -> (String) attributes.get("given_name");
            case FACEBOOK -> {
                String name = (String) attributes.get("name");
                yield name != null ? name.split(" ")[0] : "User";
            }
            case GITHUB -> {
                String name = (String) attributes.get("name");
                yield name != null && name.contains(" ") ? name.split(" ")[0] : name;
            }
            default -> "User";
        };
    }

    private String extractLastName(Map<String, Object> attributes, OAuthProvider provider) {
        log.info("Extracting last name for provider: {}", provider);
        return switch (provider) {
            case GOOGLE -> (String) attributes.get("family_name");
            case FACEBOOK -> {
                String name = (String) attributes.get("name");
                String[] parts = name != null ? name.split(" ") : new String[]{};
                yield parts.length > 1 ? parts[parts.length - 1] : "";
            }
            case GITHUB -> {
                String name = (String) attributes.get("name");
                if (name != null && name.contains(" ")) {
                    String[] parts = name.split(" ");
                    yield parts.length > 1 ? parts[parts.length - 1] : "";
                }
                yield "";
            }
            default -> "";
        };
    }
}
