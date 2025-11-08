package com.app.auth.service;

import com.app.auth.dto.request.LogoutDeviceRequest;
import com.app.auth.dto.response.DeviceSessionResponse;

import java.util.List;

public interface DeviceService {

    List<DeviceSessionResponse> getUserDeviceSessions(Long userId, String currentToken);

    void logoutDevice(LogoutDeviceRequest request, Long userId);

    void updateDeviceLastUsed(String token);
}