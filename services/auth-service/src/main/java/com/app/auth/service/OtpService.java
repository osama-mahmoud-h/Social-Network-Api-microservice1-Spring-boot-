package com.app.auth.service;

import com.app.auth.dto.request.SendOtpRequest;
import com.app.auth.dto.request.VerifyOtpRequest;
import com.app.auth.dto.response.OtpResponse;

public interface OtpService {

    OtpResponse sendOtp(SendOtpRequest request);

    OtpResponse verifyOtp(VerifyOtpRequest request);
}
