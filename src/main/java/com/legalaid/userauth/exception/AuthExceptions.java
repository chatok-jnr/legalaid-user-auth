package com.legalaid.userauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AuthExceptions {

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String email) {
            super("Email already in use: " + email);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException(String username) {
            super("Username already taken: " + username);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid email or password");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidRefreshTokenException extends RuntimeException {
        public InvalidRefreshTokenException(String reason) {
            super("Refresh token is invalid: " + reason);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String id) {
            super("User not found: " + id);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccountDisabledException extends RuntimeException {
        public AccountDisabledException() {
            super("Account is disabled");
        }
    }
}
