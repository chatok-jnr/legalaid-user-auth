package com.legalaid.userauth.service;

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
import com.legalaid.userauth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl")
class AuthServiceImplTest {

    @Mock UserRepository         userRepository;
    @Mock RoleRepository         roleRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordEncoder        passwordEncoder;
    @Mock JwtService             jwtService;
    @Mock UserDetailsService     userDetailsService;
    @Mock AuthenticationManager  authenticationManager;
    @Mock JwtProperties          jwtProperties;

    @InjectMocks AuthServiceImpl authService;

    private Role        clientRole;
    private User        testUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        clientRole = new Role((short) 1, Role.RoleName.CLIENT);

        testUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Jane Doe")
                .username("janedoe")
                .email("jane@example.com")
                .passwordHash("$2a$12$hashedpassword")
                .roles(Set.of(clientRole))
                .isActive(true)
                .isVisible(false)
                .build();

        mockUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("jane@example.com")
                .password("$2a$12$hashedpassword")
                .roles("CLIENT")
                .build();
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("should register a new user and return tokens")
        void shouldRegisterSuccessfully() {
            AuthRequests.RegisterRequest req = new AuthRequests.RegisterRequest();
            req.setFullName("Jane Doe");
            req.setUsername("janedoe");
            req.setEmail("jane@example.com");
            req.setPassword("SecurePass1!");

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(roleRepository.findByName(Role.RoleName.CLIENT)).thenReturn(Optional.of(clientRole));
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hashed");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
            when(jwtService.generateAccessToken(any())).thenReturn("access.token.here");
            when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(900_000L);
            when(jwtProperties.getRefreshTokenExpirationDays()).thenReturn(30);

            AuthResponses.TokenResponse response = authService.register(req);

            assertThat(response.getAccessToken()).isEqualTo("access.token.here");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getRefreshToken()).isNotBlank();
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw EmailAlreadyExistsException when email is taken")
        void shouldThrowOnDuplicateEmail() {
            AuthRequests.RegisterRequest req = new AuthRequests.RegisterRequest();
            req.setEmail("jane@example.com");
            req.setUsername("janedoe");

            when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(AuthExceptions.EmailAlreadyExistsException.class);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw UsernameAlreadyExistsException when username is taken")
        void shouldThrowOnDuplicateUsername() {
            AuthRequests.RegisterRequest req = new AuthRequests.RegisterRequest();
            req.setEmail("new@example.com");
            req.setUsername("janedoe");

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername("janedoe")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(AuthExceptions.UsernameAlreadyExistsException.class);
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("should authenticate and return tokens")
        void shouldLoginSuccessfully() {
            AuthRequests.LoginRequest req = new AuthRequests.LoginRequest();
            req.setEmail("jane@example.com");
            req.setPassword("SecurePass1!");

            when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(testUser));
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
            when(jwtService.generateAccessToken(any())).thenReturn("access.token.here");
            when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(900_000L);
            when(jwtProperties.getRefreshTokenExpirationDays()).thenReturn(30);

            AuthResponses.TokenResponse response = authService.login(req);

            assertThat(response.getAccessToken()).isNotBlank();
            verify(refreshTokenRepository).revokeAllActiveTokensForUser(testUser);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException on bad password")
        void shouldThrowOnBadCredentials() {
            AuthRequests.LoginRequest req = new AuthRequests.LoginRequest();
            req.setEmail("jane@example.com");
            req.setPassword("WrongPassword!");

            doThrow(new BadCredentialsException("bad"))
                    .when(authenticationManager).authenticate(any());

            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(AuthExceptions.InvalidCredentialsException.class);
        }
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("refreshToken()")
    class RefreshTokenTests {

        @Test
        @DisplayName("should rotate token and return new pair")
        void shouldRotateRefreshToken() {
            RefreshToken stored = RefreshToken.builder()
                    .id(UUID.randomUUID())
                    .user(testUser)
                    .token("valid-refresh-token")
                    .expiresAt(OffsetDateTime.now().plusDays(1))
                    .build();

            AuthRequests.RefreshTokenRequest req = new AuthRequests.RefreshTokenRequest();
            req.setRefreshToken("valid-refresh-token");

            when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(stored));
            when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
            when(jwtService.generateAccessToken(any())).thenReturn("new.access.token");
            when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(900_000L);
            when(jwtProperties.getRefreshTokenExpirationDays()).thenReturn(30);

            AuthResponses.TokenResponse response = authService.refreshToken(req);

            assertThat(response.getAccessToken()).isEqualTo("new.access.token");
            assertThat(stored.getRevokedAt()).isNotNull();  // old token revoked
        }

        @Test
        @DisplayName("should revoke all tokens and throw on reuse of revoked token")
        void shouldDetectTokenReuse() {
            RefreshToken revoked = RefreshToken.builder()
                    .id(UUID.randomUUID())
                    .user(testUser)
                    .token("reused-token")
                    .expiresAt(OffsetDateTime.now().plusDays(1))
                    .revokedAt(OffsetDateTime.now().minusHours(1))  // already revoked
                    .build();

            AuthRequests.RefreshTokenRequest req = new AuthRequests.RefreshTokenRequest();
            req.setRefreshToken("reused-token");

            when(refreshTokenRepository.findByToken("reused-token")).thenReturn(Optional.of(revoked));

            assertThatThrownBy(() -> authService.refreshToken(req))
                    .isInstanceOf(AuthExceptions.InvalidRefreshTokenException.class)
                    .hasMessageContaining("revoked");

            verify(refreshTokenRepository).revokeAllActiveTokensForUser(testUser);
        }
    }
}
