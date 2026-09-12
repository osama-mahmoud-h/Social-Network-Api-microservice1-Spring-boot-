package com.app.auth.event.listener;

import com.app.auth.model.dto.request.SendOtpRequest;
import com.app.auth.model.enums.OtpType;
import com.app.auth.event.UserRegisteredEvent;
import com.app.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventListener {

    private final OtpService otpService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistrationForOtp(UserRegisteredEvent event) {
        log.info("User Registration committed for {}. Asynchronously generating and sending OTP...", event.email());
        
        SendOtpRequest otpRequest = SendOtpRequest.builder()
                .email(event.email())
                .type(OtpType.REGISTRATION)
                .build();
                
        otpService.sendOtp(otpRequest);
    }
}
