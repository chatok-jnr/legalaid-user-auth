package com.legalaid.userauth.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.*;

public class ClientProfileRequest {
    @Data
    public static class RegisterRequest{
        @NotNull(message = "User id is required")
        private UUID userId;

        @Size(max = 100)
        private String emergencyContactName;

        @Size(max = 20)
        private String emergencyContactPhone;

        private String notes;
    }

    @Data
    public static class UpdateRequest{
        @NotNull(message = "User id is required")
        private UUID userId;

        @Size(max = 100)
        private String emergencyContactName;

        @Size(max = 20)
        private String emergencyContactPhone;

        private String notes;
    }
}
