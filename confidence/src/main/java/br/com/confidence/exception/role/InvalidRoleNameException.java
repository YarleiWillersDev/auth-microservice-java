package br.com.confidence.exception.role;

public class InvalidRoleNameException extends RuntimeException {
    public InvalidRoleNameException(String message) {
        super(message);
    }
}
