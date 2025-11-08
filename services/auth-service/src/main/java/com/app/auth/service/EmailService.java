package com.app.auth.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode, String purpose);
}
