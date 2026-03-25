package com.legalaid.userauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AuthExceptions {
    // ---------------- User ------------------
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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class InvalidRoleException extends RuntimeException {
        public InvalidRoleException(String message) {
            super(message);
        }
    }

    // ---------------- Client ------------------
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException() {
            super("Role not found");
        }
    }

    // ---------------- Client ------------------
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ClientAlreadyExistException extends RuntimeException {
        public ClientAlreadyExistException() {
            super("Client profile already exists for this user");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ClientNotFoundException extends RuntimeException {
        public ClientNotFoundException() {
            super("Client profile not found for this user");
        }
    }

    // ---------------- Lawyer ------------------
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class LawyerAlreadyExistException extends RuntimeException {
        public LawyerAlreadyExistException() {
            super("Lawyer profile already exists for this user");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class LawyerNotFoundException extends RuntimeException {
        public LawyerNotFoundException() {
            super("Lawyer profile not found for this user");
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() {
            super("Unauthorized to perform this action");
        }
    }
}
