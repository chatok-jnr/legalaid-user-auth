package com.legalaid.userauth.controller;

import com.legalaid.userauth.dto.request.AuthRequests;
import com.legalaid.userauth.dto.response.AuthResponses;
import com.legalaid.userauth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── POST /auth/register ───────────────────────────────────────────────────

    /**
     * Public — register a new user.
     * Returns a full token pair so the user is immediately logged in.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponses.TokenResponse> register(
            @Valid @RequestBody AuthRequests.RegisterRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // ── POST /auth/login ──────────────────────────────────────────────────────

    /**
     * Public — authenticate with email + password.
     * Returns an access token (15 min) and a rotating refresh token (30 days).
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponses.TokenResponse> login(
            @Valid @RequestBody AuthRequests.LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ── POST /auth/refresh ────────────────────────────────────────────────────

    /**
     * Public — exchange a valid refresh token for a new token pair.
     * The supplied refresh token is immediately revoked (rotation).
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponses.TokenResponse> refresh(
            @Valid @RequestBody AuthRequests.RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // ── GET /auth/me ──────────────────────────────────────────────────────────

    /**
     * Protected (any authenticated user) — fetch own profile.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponses.UserResponse> getProfile(
            @AuthenticationPrincipal UserDetails principal
    ) {
        return ResponseEntity.ok(authService.getProfile(principal.getUsername()));
    }

    // ── PATCH /auth/me ────────────────────────────────────────────────────────

    /**
     * Protected (any authenticated user) — update own profile fields.
     * Uses PATCH semantics: only non-null fields are applied.
     */
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponses.UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody AuthRequests.UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(authService.updateProfile(principal.getUsername(), request));
    }
}
