package com.legalaid.userauth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Validation errors ─────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "Invalid value" : fe.getDefaultMessage(),
                        (a, b) -> a   // keep first on duplicates
                ));

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Failed");
        pd.setDetail("One or more fields are invalid");
        pd.setProperty("errors", errors);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // ── Domain exceptions ─────────────────────────────────────────────────────

    @ExceptionHandler(AuthExceptions.EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailConflict(AuthExceptions.EmailAlreadyExistsException ex) {
        return buildProblem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.UsernameAlreadyExistsException.class)
    public ProblemDetail handleUsernameConflict(AuthExceptions.UsernameAlreadyExistsException ex) {
        return buildProblem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(AuthExceptions.InvalidCredentialsException ex) {
        return buildProblem(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.InvalidRefreshTokenException.class)
    public ProblemDetail handleInvalidRefreshToken(AuthExceptions.InvalidRefreshTokenException ex) {
        return buildProblem(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(AuthExceptions.UserNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.AccountDisabledException.class)
    public ProblemDetail handleAccountDisabled(AuthExceptions.AccountDisabledException ex) {
        return buildProblem(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.ClientAlreadyExistException.class)
    public ProblemDetail handleClientAlreadyExists(AuthExceptions.ClientAlreadyExistException ex) {
        return buildProblem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.ClientNotFoundException.class)
    public ProblemDetail handleClientNotFound(AuthExceptions.ClientNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.LawyerAlreadyExistException.class)
    public ProblemDetail handleLawyerAlreadyExists(AuthExceptions.LawyerAlreadyExistException ex) {
        return buildProblem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.LawyerNotFoundException.class)
    public ProblemDetail handleLawyerNotFound(AuthExceptions.LawyerNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.UnauthorizedException.class)
    public ProblemDetail handleUnauthorized(AuthExceptions.UnauthorizedException ex) {
        return buildProblem(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.InvalidRoleException.class)
    public ProblemDetail handleInvalidRole(AuthExceptions.InvalidRoleException ex) {
        return buildProblem(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.AddressTypeRequiredException.class)
    public ProblemDetail handleAddressTypeRequired(AuthExceptions.AddressTypeRequiredException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.AddressAlreadyExistsException.class)
    public ProblemDetail handleAddressAlreadyExists(AuthExceptions.AddressAlreadyExistsException ex) {
        return buildProblem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.AddressNotFoundException.class)
    public ProblemDetail handleAddressNotFound(AuthExceptions.AddressNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── Spring Security exceptions ────────────────────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        return buildProblem(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return buildProblem(HttpStatus.FORBIDDEN, "You do not have permission to access this resource");
    }

    // ── Fallback ──────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ProblemDetail buildProblem(HttpStatus status, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(status.getReasonPhrase());
        pd.setDetail(detail);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
