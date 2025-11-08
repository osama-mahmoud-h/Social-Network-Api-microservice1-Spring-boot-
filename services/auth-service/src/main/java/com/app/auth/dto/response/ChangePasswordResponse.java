package com.app.auth.dto.response;

import com.app.auth.enums.PasswordChangeResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    private PasswordChangeResult result;
    private String message;

    public static ChangePasswordResponse success() {
        return ChangePasswordResponse.builder()
                .result(PasswordChangeResult.SUCCESS)
                .message("Password changed successfully. All active sessions have been logged out.")
                .build();
    }

    public static ChangePasswordResponse failed(PasswordChangeResult result, String message) {
        return ChangePasswordResponse.builder()
                .result(result)
                .message(message)
                .build();
    }
}
