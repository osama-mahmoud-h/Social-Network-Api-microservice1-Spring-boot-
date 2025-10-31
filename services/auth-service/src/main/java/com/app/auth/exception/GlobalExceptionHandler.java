package com.app.auth.exception;

import com.app.shared.security.dto.MyApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Custom Exceptions

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MyApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                ex.getMessage(),
                "USER_ALREADY_EXISTS",
                "A user with this email already exists in the system"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MyApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                ex.getMessage(),
                "USER_NOT_FOUND",
                "The requested user could not be found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<MyApiResponse<Void>> handleInvalidToken(InvalidTokenException ex) {
        log.error("Invalid token: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                ex.getMessage(),
                "INVALID_TOKEN",
                "The provided token is invalid"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<MyApiResponse<Void>> handleTokenExpired(TokenExpiredException ex) {
        log.error("Token expired: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                ex.getMessage(),
                "TOKEN_EXPIRED",
                "The provided token has expired"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Spring Security Exceptions

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MyApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "Invalid email or password",
                "BAD_CREDENTIALS",
                "The provided credentials are incorrect"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<MyApiResponse<Void>> handleDisabled(DisabledException ex) {
        log.error("Account disabled: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "Account is disabled. Please contact support.",
                "ACCOUNT_DISABLED",
                "This account has been disabled"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<MyApiResponse<Void>> handleLocked(LockedException ex) {
        log.error("Account locked: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "Account is locked. Please contact support.",
                "ACCOUNT_LOCKED",
                "This account has been locked"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<MyApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication failed: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "Authentication failed",
                "AUTHENTICATION_FAILED",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // JWT Exceptions

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<MyApiResponse<Void>> handleExpiredJwt(ExpiredJwtException ex) {
        log.error("JWT token expired: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "JWT token has expired",
                "JWT_EXPIRED",
                "The JWT token has expired and needs to be refreshed"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<MyApiResponse<Void>> handleMalformedJwt(MalformedJwtException ex) {
        log.error("Malformed JWT token: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "Malformed JWT token",
                "JWT_MALFORMED",
                "The JWT token format is invalid"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<MyApiResponse<Void>> handleSignatureException(SignatureException ex) {
        log.error("Invalid JWT signature: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "Invalid JWT signature",
                "JWT_SIGNATURE_INVALID",
                "The JWT token signature is invalid"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<MyApiResponse<Void>> handleJwtException(JwtException ex) {
        log.error("JWT error: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "JWT token error",
                "JWT_ERROR",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Validation Exceptions

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MyApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());

        List<Map<String, String>> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    Map<String, String> errorMap = new HashMap<>();
                    String fieldName = error instanceof FieldError ?
                            ((FieldError) error).getField() : error.getObjectName();
                    errorMap.put("field", fieldName);
                    errorMap.put("message", error.getDefaultMessage());
                    return errorMap;
                })
                .collect(Collectors.toList());

        MyApiResponse<Void> response = MyApiResponse.error(
                "Validation failed",
                "VALIDATION_ERROR",
                "One or more fields have validation errors",
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MyApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'",
                ex.getValue(),
                ex.getName());

        String details = String.format("Expected type: %s",
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        MyApiResponse<Void> response = MyApiResponse.error(
                message,
                "TYPE_MISMATCH",
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // General Exceptions

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MyApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                ex.getMessage(),
                "ILLEGAL_ARGUMENT",
                "The provided argument is invalid"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MyApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                ex.getMessage(),
                "ILLEGAL_STATE",
                "The operation cannot be completed in the current state"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<MyApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.error("No handler found: {}", ex.getMessage());

        MyApiResponse<Void> response = MyApiResponse.error(
                "The requested endpoint does not exist",
                "ENDPOINT_NOT_FOUND",
                String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL())
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MyApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);

        MyApiResponse<Void> response = MyApiResponse.error(
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred on the server"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}