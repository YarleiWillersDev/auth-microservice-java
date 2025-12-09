package br.com.confidence.exception.user;

public class CurrentPasswordIncorrectException extends RuntimeException {
    public CurrentPasswordIncorrectException(String message) {
        super(message);
    }
}
