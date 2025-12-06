package br.com.confidence.exception.user;

public class InvalidUsernameException extends RuntimeException {

    public InvalidUsernameException(String message) {
        super(message);
    }

}
