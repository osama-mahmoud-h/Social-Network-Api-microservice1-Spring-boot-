package com.app.auth.service;

import com.app.auth.model.dto.request.RegisterRequest;
import com.app.auth.model.dto.response.RegistrationResponse;

public interface UserRegistrationService {
    RegistrationResponse registerUser(RegisterRequest request);
}
