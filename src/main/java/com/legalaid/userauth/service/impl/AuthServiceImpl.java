package com.legalaid.userauth.service.impl;

import com.legalaid.userauth.config.JwtProperties;
import com.legalaid.userauth.dto.request.AuthRequests;
import com.legalaid.userauth.dto.response.AuthResponses;
import com.legalaid.userauth.entity.RefreshToken;
import com.legalaid.userauth.entity.Role;
import com.legalaid.userauth.entity.User;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.RefreshTokenRepository;
import com.legalaid.userauth.repository.RoleRepository;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.security.JwtService;
import com.legalaid.userauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final RoleRepository        roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;
    private final UserDetailsService    userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties         jwtProperties;

    // ── Register ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponses.TokenResponse register(AuthRequests.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthExceptions.EmailAlreadyExistsException(request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthExceptions.UsernameAlreadyExistsException(request.getUsername());
        }

        // Resolve roles — default to CLIENT if none supplied
        Set<Role> roles = resolveRoles(request.getRoles());

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .preferredLanguage(request.getPreferredLanguage())
                .roles(roles)
                .gender(request.getGender())
                .build();

        userRepository.save(user);
        log.info("New user registered: {} ({})", user.getUsername(), user.getEmail());

        return buildTokenResponse(user);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponses.TokenResponse login(AuthRequests.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException e) {
            throw new AuthExceptions.AccountDisabledException();
        } catch (BadCredentialsException e) {
            throw new AuthExceptions.InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(AuthExceptions.InvalidCredentialsException::new);

        // Revoke all existing refresh tokens for this user (single-session policy)
        refreshTokenRepository.revokeAllActiveTokensForUser(user);

        log.info("User logged in: {}", user.getEmail());
        return buildTokenResponse(user);
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponses.TokenResponse refreshToken(AuthRequests.RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthExceptions.InvalidRefreshTokenException("token not found"));

        if (storedToken.isRevoked()) {
            // Possible token reuse attack — revoke all tokens for this user
            refreshTokenRepository.revokeAllActiveTokensForUser(storedToken.getUser());
            log.warn("Refresh token reuse detected for user: {}", storedToken.getUser().getEmail());
            throw new AuthExceptions.InvalidRefreshTokenException("token has been revoked");
        }

        if (storedToken.isExpired()) {
            throw new AuthExceptions.InvalidRefreshTokenException("token has expired");
        }

        // Rotate: revoke the used token, issue a new pair
        storedToken.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();
        log.info("Refresh token rotated for user: {}", user.getEmail());
        return buildTokenResponse(user);
    }

    // ── Get Profile ───────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public AuthResponses.UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(email));
        return toUserResponse(user);
    }

    // ── Update Profile ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponses.UserResponse updateProfile(String email, AuthRequests.UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(email));

        if (request.getFullName()          != null) user.setFullName(request.getFullName());
        if (request.getPhone()             != null) user.setPhone(request.getPhone());
        if (request.getProfilePicUrl()     != null) user.setProfilePicUrl(request.getProfilePicUrl());
        if (request.getDateOfBirth()       != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getPreferredLanguage() != null) user.setPreferredLanguage(request.getPreferredLanguage());
        if (request.getIsVisible()         != null) user.setIsVisible(request.getIsVisible());

        userRepository.save(user);
        log.info("Profile updated for user: {}", user.getEmail());
        return toUserResponse(user);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private AuthResponses.TokenResponse buildTokenResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken  = jwtService.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(user);

        return AuthResponses.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpirationMs() / 1000)
                .user(toUserResponse(user))
                .build();
    }

    private String createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(OffsetDateTime.now().plusDays(jwtProperties.getRefreshTokenExpirationDays()))
                .build();
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    private Set<Role> resolveRoles(Set<Role.RoleName> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            Role clientRole = roleRepository.findByName(Role.RoleName.CLIENT)
                    .orElseThrow(() -> new IllegalStateException("Default role CLIENT not found in DB"));
            return Set.of(clientRole);
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new IllegalStateException("Role not found: " + name)))
                .collect(Collectors.toSet());
    }

    private AuthResponses.UserResponse toUserResponse(User user) {
        return AuthResponses.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePicUrl(user.getProfilePicUrl())
                .dateOfBirth(user.getDateOfBirth())
                .preferredLanguage(user.getPreferredLanguage())
                .isVisible(user.getIsVisible())
                .isActive(user.getIsActive())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .sorted()
                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
