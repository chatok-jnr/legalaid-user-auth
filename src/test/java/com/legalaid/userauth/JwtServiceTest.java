package com.legalaid.userauth.security;

import com.legalaid.userauth.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService")
class JwtServiceTest {

    private JwtService jwtService;

    // Raw string secret (≥256 bits equivalent)
    private static final String SECRET =
            "ThisIsAVeryLongSecretKeyForLegalAidPlatformThatMustBeAtLeast256BitsLong!";

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret(SECRET);
        props.setAccessTokenExpirationMs(900_000L);   // 15 min
        props.setRefreshTokenExpirationDays(30);
        jwtService = new JwtService(props);
    }

    private UserDetails buildUser(String email, String... roles) {
        return User.builder()
                .username(email)
                .password("irrelevant")
                .roles(roles)
                .build();
    }

    @Test
    @DisplayName("should generate a non-blank access token")
    void shouldGenerateToken() {
        UserDetails ud = buildUser("jane@example.com", "CLIENT");
        String token = jwtService.generateAccessToken(ud);
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("should extract correct subject from token")
    void shouldExtractSubject() {
        UserDetails ud = buildUser("jane@example.com", "CLIENT");
        String token = jwtService.generateAccessToken(ud);
        assertThat(jwtService.extractSubject(token)).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("should embed roles claim in token")
    void shouldEmbedRoles() {
        UserDetails ud = buildUser("lawyer@example.com", "LAWYER");
        String token = jwtService.generateAccessToken(ud);
        List<String> roles = jwtService.extractRoles(token);
        assertThat(roles).anyMatch(r -> r.contains("LAWYER"));
    }

    @Test
    @DisplayName("isTokenValid should return true for matching user and fresh token")
    void shouldValidateCorrectToken() {
        UserDetails ud = buildUser("jane@example.com", "CLIENT");
        String token = jwtService.generateAccessToken(ud);
        assertThat(jwtService.isTokenValid(token, ud)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid should return false for wrong user")
    void shouldRejectTokenForWrongUser() {
        UserDetails ud1 = buildUser("jane@example.com", "CLIENT");
        UserDetails ud2 = buildUser("other@example.com", "CLIENT");
        String token = jwtService.generateAccessToken(ud1);
        assertThat(jwtService.isTokenValid(token, ud2)).isFalse();
    }
}
