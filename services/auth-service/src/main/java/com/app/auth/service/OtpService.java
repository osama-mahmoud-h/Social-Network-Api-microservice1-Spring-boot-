package com.app.auth.service;

import com.app.auth.model.dto.request.SendOtpRequest;
import com.app.auth.model.dto.request.VerifyOtpRequest;
import com.app.auth.model.dto.response.OtpResponse;
import com.app.auth.model.entity.Otp;

public interface OtpService {

    OtpResponse sendOtp(SendOtpRequest request);

    OtpResponse verifyOtp(VerifyOtpRequest request);
    Otp generateOtp(SendOtpRequest request);
}
