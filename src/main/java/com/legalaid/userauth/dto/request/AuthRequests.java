package com.legalaid.userauth.dto.request;

import com.legalaid.userauth.entity.Role;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

public class AuthRequests {

    @Data
    public static class RegisterRequest {

        @NotBlank(message = "Full name is required")
        @Size(max = 100)
        private String fullName;

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username may only contain letters, digits, underscores, dots and hyphens")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        @Size(max = 255)
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        private String password;

        @Size(max = 20)
        private String phone;

        private LocalDate dateOfBirth;

        @NotBlank(message = "Gender is required")
        private String gender;

        @Size(max = 50)
        private String preferredLanguage;

        /** Roles to assign; defaults to CLIENT if omitted */
        private Set<Role.RoleName> roles;
    }

    @Data
    public static class LoginRequest {

        @NotBlank(message = "Email is required")
        @Email
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class RefreshTokenRequest {

        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @Data
    public static class UpdateProfileRequest {

        @Size(max = 100)
        private String fullName;

        @Size(max = 20)
        private String phone;

        @Size(max = 500)
        private String profilePicUrl;

        private LocalDate dateOfBirth;

        @Size(max = 50)
        private String preferredLanguage;

        private Boolean isVisible;
    }
}
