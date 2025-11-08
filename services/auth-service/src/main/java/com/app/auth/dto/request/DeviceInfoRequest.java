package com.app.auth.dto.request;

import com.app.auth.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoRequest {
    private String deviceName;
    private DeviceType deviceType;
    private String ipAddress;
    private String userAgent;
}