package com.legalaid.userauth.dto.response.admin;

import com.legalaid.userauth.entity.admin.AdminActionType;
import com.legalaid.userauth.entity.admin.AdminStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AdminResponse {

    @Data
    @Builder
    public static class AdminProfileResponse {
        private UUID id;
        private UUID refById;
        private AdminStatus status;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    @Builder
    public static class AdminActionLogResponse {
        private UUID id;
        private AdminActionType actionType;
        private UUID targetId;
        private String reason;
        private UUID adminId;
        private String ipAddress;
        private String userAgent;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
