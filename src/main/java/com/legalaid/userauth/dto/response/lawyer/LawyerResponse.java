package com.legalaid.userauth.dto.response.lawyer;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

public class LawyerResponse {

    @Data
    @Builder
    public static class Document{
        private UUID id;
        private String credentialType;
        private String title;
        private String issuingBody;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String url;
    }

    @Data
    @Builder
    public static class LawyerProfileResponse {
        private String barNumber;
        private String bio;
        private List<String> specializations;
        private short yearsExperience;
        private boolean isVerified;

        List<Document> credentials;
    }

    @Data
    @Builder
    public static class DocumentUploadResponse {
        private String documentUrl;
    }
}
