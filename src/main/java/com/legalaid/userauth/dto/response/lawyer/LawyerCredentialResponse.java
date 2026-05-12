package com.legalaid.userauth.dto.response.lawyer;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class LawyerCredentialResponse {
    @Data
    @Builder
    public static class Create{
        private UUID id;
        private UUID lawyerId;
        private String credentialType;
        private String title;
        private String issuingBody;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String documentUrl;
        private Instant createdAt;
    }


}
