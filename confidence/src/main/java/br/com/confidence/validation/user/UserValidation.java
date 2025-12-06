package br.com.confidence.validation.user;

import org.springframework.stereotype.Component;

import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.exception.user.InvalidUserEmailException;
import br.com.confidence.exception.user.InvalidUserPasswordException;
import br.com.confidence.exception.user.InvalidUsernameException;

@Component
public class UserValidation {

    public void validateUserRequest(UserRequest userRequest) {
        validateNameUserRequest(userRequest.name());
        validateEmailUserRequest(userRequest.email());
        validatePasswordUserRequest(userRequest.password());
    }

    public void validateNameUserRequest(String name) {
        if (name == null || name.isBlank() || name.length() < 3) {
            throw new InvalidUsernameException("Username cannot be null/empty or contain fewer than 3 letters.");
        }
    }

    public void validateEmailUserRequest(String email) {
        if (email == null || email.isBlank() || email.length() < 15) {
            throw new InvalidUserEmailException(
                    "Invalid user email address. The email address must contain at least 15 characters.");
        }
    }

    public void validatePasswordUserRequest(String password) {
        validateEmptyOrNullPasswordUserRequest(password);
        validatePasswordLength(password);
        validateUppercaseLetterInPasswordUserRequest(password);
        validateLowercaseLetterInPasswordUserRequest(password);
        validateNumberContainedInPasswordUserRequest(password);
        validateSpecialCharacterInPasswordUserRequest(password);
    }

    public void validateEmptyOrNullPasswordUserRequest(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidUserPasswordException("Password cannot be null or empty");
        }
    }

    public void validatePasswordLength(String password) {
        if (password.length() < 8) {
            throw new InvalidUserPasswordException("Password must have at least 8 characters");
        }
    }

    public void validateUppercaseLetterInPasswordUserRequest(String password) {
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidUserPasswordException("Password must contain at least one uppercase letter");
        }
    }

    public void validateLowercaseLetterInPasswordUserRequest(String password) {
        if (!password.matches(".*[a-z].*")) {
            throw new InvalidUserPasswordException("Password must contain at least one digit");
        }
    }

    public void validateNumberContainedInPasswordUserRequest(String password) {
        if (!password.matches(".*\\d.*")) {
            throw new InvalidUserPasswordException("Password must contain at least one digit");
        }
    }

    public void validateSpecialCharacterInPasswordUserRequest(String password) {
        if (!password.matches(".*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/].*")) {
            throw new InvalidUserPasswordException("Password must contain at least one special character");
        }
    }
}
