package br.com.confidence.exception.auth;

public class PasswordResetTokenExpiredException extends RuntimeException {
    public PasswordResetTokenExpiredException(String message) {
        super(message);
    }
}
