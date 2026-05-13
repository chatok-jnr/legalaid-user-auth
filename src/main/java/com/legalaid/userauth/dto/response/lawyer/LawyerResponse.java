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


    // ====================
    // For Admin

    @Data
    @Builder
    public static class Doc4Admin{
        private String credentialType;
        private String title;
        private String issuingBody;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String url;
    }

    @Data
    @Builder
    public static class LawyerDetailsForAdmin{
        private UUID id;
        private String profilePicUlr;
        private String fullName;
        private String email;
        private List<String> specializations;
        private short experience;
        private Instant applied;
        private Boolean isVerified;
        private Instant verifiedAt;
        private UUID verifiedBy;
        List<Doc4Admin> documents;
    }
}
