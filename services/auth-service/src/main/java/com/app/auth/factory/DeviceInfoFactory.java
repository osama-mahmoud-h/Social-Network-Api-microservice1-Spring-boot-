package com.app.auth.factory;

import com.app.auth.dto.request.DeviceInfoRequest;
import com.app.auth.enums.DeviceType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DeviceInfoFactory {

    public DeviceInfoRequest extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = extractIpAddress(request);
        DeviceType deviceType = detectDeviceType(userAgent);
        String deviceName = extractDeviceName(userAgent, deviceType);

        return DeviceInfoRequest.builder()
                .deviceName(deviceName)
                .deviceType(deviceType)
                .ipAddress(ipAddress)
                .userAgent(userAgent != null ? userAgent : "Unknown")
                .build();
    }

    private String extractIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For (first one is the client)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    private DeviceType detectDeviceType(String userAgent) {
        if (userAgent == null) {
            return DeviceType.UNKNOWN;
        }

        String lowerUserAgent = userAgent.toLowerCase();

        // Check for mobile devices
        if (lowerUserAgent.contains("mobile") ||
            lowerUserAgent.contains("android") ||
            lowerUserAgent.contains("iphone") ||
            lowerUserAgent.contains("ipod") ||
            lowerUserAgent.contains("blackberry") ||
            lowerUserAgent.contains("windows phone")) {
            return DeviceType.MOBILE;
        }

        // Check for tablets
        if (lowerUserAgent.contains("tablet") ||
            lowerUserAgent.contains("ipad")) {
            return DeviceType.TABLET;
        }

        // Check if it's a web browser
        if (lowerUserAgent.contains("mozilla") ||
            lowerUserAgent.contains("chrome") ||
            lowerUserAgent.contains("safari") ||
            lowerUserAgent.contains("firefox") ||
            lowerUserAgent.contains("edge") ||
            lowerUserAgent.contains("opera")) {
            return DeviceType.WEB;
        }

        // Default to desktop for other cases
        return DeviceType.DESKTOP;
    }

    private String extractDeviceName(String userAgent, DeviceType deviceType) {
        if (userAgent == null) {
            return "Unknown Device";
        }

        String lowerUserAgent = userAgent.toLowerCase();

        // Extract browser name for web/desktop
        if (deviceType == DeviceType.WEB || deviceType == DeviceType.DESKTOP) {
            if (lowerUserAgent.contains("edg/")) {
                return "Microsoft Edge";
            } else if (lowerUserAgent.contains("chrome/")) {
                return "Google Chrome";
            } else if (lowerUserAgent.contains("firefox/")) {
                return "Mozilla Firefox";
            } else if (lowerUserAgent.contains("safari/") && !lowerUserAgent.contains("chrome")) {
                return "Safari";
            } else if (lowerUserAgent.contains("opera/") || lowerUserAgent.contains("opr/")) {
                return "Opera";
            }
            return "Web Browser";
        }

        // Extract mobile device name
        if (deviceType == DeviceType.MOBILE) {
            if (lowerUserAgent.contains("iphone")) {
                return "iPhone";
            } else if (lowerUserAgent.contains("android")) {
                return "Android Device";
            } else if (lowerUserAgent.contains("windows phone")) {
                return "Windows Phone";
            } else if (lowerUserAgent.contains("blackberry")) {
                return "BlackBerry";
            }
            return "Mobile Device";
        }

        // Extract tablet name
        if (deviceType == DeviceType.TABLET) {
            if (lowerUserAgent.contains("ipad")) {
                return "iPad";
            } else if (lowerUserAgent.contains("android")) {
                return "Android Tablet";
            }
            return "Tablet";
        }

        return "Unknown Device";
    }
}