package com.app.auth.service.impl;

import com.app.auth.dto.request.LogoutDeviceRequest;
import com.app.auth.dto.response.DeviceSessionResponse;
import com.app.auth.exception.UnauthorizedAccessException;
import com.app.auth.mapper.DeviceMapper;
import com.app.auth.model.Token;
import com.app.auth.repository.TokenRepository;
import com.app.auth.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    private final TokenRepository tokenRepository;
    private final DeviceMapper deviceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DeviceSessionResponse> getUserDeviceSessions(Long userId, String currentToken) {
        List<Token> tokens = tokenRepository.findAllValidTokenByUser(userId);
        return deviceMapper.mapToDeviceSessionResponseList(tokens, currentToken);
    }

    @Override
    @Transactional
    public void logoutDevice(LogoutDeviceRequest request, Long userId) {
        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new RuntimeException("Token not found"));

        // Verify that the token belongs to the user
        if (!token.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to logout this device");
        }

        tokenRepository.delete(token);
        log.info("Device session {} logged out for user {}", request.getTokenId(), userId);
    }

    @Override
    @Transactional
    public void updateDeviceLastUsed(String tokenString) {
        tokenRepository.findByToken(tokenString).ifPresent(token -> {
            token.setLastUsedAt(Instant.now());
            tokenRepository.save(token);
        });
    }
}