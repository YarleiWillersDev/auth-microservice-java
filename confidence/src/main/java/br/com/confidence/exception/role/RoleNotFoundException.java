package br.com.confidence.exception.role;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }
    
}