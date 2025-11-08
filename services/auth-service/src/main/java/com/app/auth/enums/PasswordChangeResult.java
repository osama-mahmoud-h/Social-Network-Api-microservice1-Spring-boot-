package com.app.auth.enums;

public enum PasswordChangeResult {
    SUCCESS,
    INVALID_OLD_PASSWORD,
    WEAK_PASSWORD,
    PASSWORDS_DO_NOT_MATCH,
    SAME_AS_OLD_PASSWORD
}