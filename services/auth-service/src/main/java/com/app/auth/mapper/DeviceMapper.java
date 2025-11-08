package com.app.auth.mapper;

import com.app.auth.dto.request.DeviceInfoRequest;
import com.app.auth.dto.response.DeviceSessionResponse;
import com.app.auth.model.Token;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeviceMapper {

    public DeviceSessionResponse mapToDeviceSessionResponse(Token token, String currentToken) {
        return DeviceSessionResponse.builder()
                .tokenId(token.getTokenId())
                .deviceName(token.getDeviceName())
                .deviceType(token.getDeviceType())
                .ipAddress(token.getIpAddress())
                .createdAt(token.getCreatedAt())
                .lastUsedAt(token.getLastUsedAt())
                .expiresAt(token.getExpiresAt())
                .currentDevice(token.getToken().equals(currentToken))
                .build();
    }

    public List<DeviceSessionResponse> mapToDeviceSessionResponseList(List<Token> tokens, String currentToken) {
        return tokens.stream()
                .map(token -> mapToDeviceSessionResponse(token, currentToken))
                .collect(Collectors.toList());
    }

    public Token.TokenBuilder enrichTokenWithDeviceInfo(Token.TokenBuilder builder, DeviceInfoRequest deviceInfo) {
        if (deviceInfo != null) {
            return builder
                    .deviceName(deviceInfo.getDeviceName())
                    .deviceType(deviceInfo.getDeviceType())
                    .ipAddress(deviceInfo.getIpAddress())
                    .userAgent(deviceInfo.getUserAgent())
                    .lastUsedAt(java.time.Instant.now());
        }
        return builder;
    }
}