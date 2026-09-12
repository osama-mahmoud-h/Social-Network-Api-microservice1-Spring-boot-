package com.app.auth.model.dto.response;

import com.app.auth.model.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSessionResponse {
    private Long tokenId;
    private String deviceName;
    private DeviceType deviceType;
    private String ipAddress;
    private Instant createdAt;
    private Instant lastUsedAt;
    private Instant expiresAt;
    private boolean currentDevice;
}