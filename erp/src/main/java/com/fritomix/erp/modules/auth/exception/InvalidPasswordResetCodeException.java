package com.fritomix.erp.modules.auth.exception;

public class InvalidPasswordResetCodeException extends RuntimeException {
    public InvalidPasswordResetCodeException(String message) {
        super(message);
    }
}
