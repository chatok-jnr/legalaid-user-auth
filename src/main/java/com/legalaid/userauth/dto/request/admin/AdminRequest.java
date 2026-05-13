package com.legalaid.userauth.dto.request.admin;

import com.legalaid.userauth.entity.admin.AdminActionType;
import com.legalaid.userauth.entity.admin.AdminStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

public class AdminRequest {

    @Data
    public static class RegisterAdminProfile {
        @NotNull(message = "User id is required")
        private UUID userId;

        private UUID refBy;

        private AdminStatus status;
    }

    @Data
    public static class UpdateAdminStatus {
        @NotNull(message = "User id is required")
        private UUID userId;

        @NotNull(message = "Status is required")
        private AdminStatus status;
    }

    @Data
    public static class CreateActionLog {
        @NotNull(message = "Action type is required")
        private AdminActionType actionType;

        @NotNull(message = "Target id is required")
        private UUID targetId;

        @NotBlank(message = "Reason is required")
        private String reason;
    }
}
