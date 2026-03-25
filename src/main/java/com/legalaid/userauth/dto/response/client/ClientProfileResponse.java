package com.legalaid.userauth.dto.response.client;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

public class ClientProfileResponse {

    @Data
    @Builder
    public static class ProfileResponse {
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String notes;
        private OffsetDateTime createdAt;
    }
}
