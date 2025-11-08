package com.app.auth.service;

import com.app.auth.dto.request.ChangePasswordRequest;
import com.app.auth.dto.response.ChangePasswordResponse;

public interface PasswordService {

    ChangePasswordResponse changePassword(ChangePasswordRequest request, Long userId);
}
