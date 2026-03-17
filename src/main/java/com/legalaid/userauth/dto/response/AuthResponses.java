package com.legalaid.userauth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponses {

    @Data
    @Builder
    public static class TokenResponse {
        private String        accessToken;
        private String        refreshToken;
        private String        tokenType;
        private long          expiresIn;   // seconds
        private UserResponse  user;
    }

    @Data
    @Builder
    public static class UserResponse {
        private UUID           id;
        private String         fullName;
        private String         username;
        private String         email;
        private String         phone;
        private String         profilePicUrl;
        private LocalDate      dateOfBirth;
        private String         preferredLanguage;
        private Boolean        isVisible;
        private Boolean        isActive;
        private List<String>   roles;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
    }

    @Data
    @Builder
    public static class MessageResponse {
        private String message;
    }
}
