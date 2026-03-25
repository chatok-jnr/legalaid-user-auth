package com.legalaid.userauth.dto.response.lawyer;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

public class LawyerResponse {

    @Data
    @Builder
    public static class LawyerProfileResponse {
        private String barNumber;
        private String bio;
        private List<String> specializations;
        private short yearsExperience;
        private BigDecimal consultationFee;
        private boolean isVerified;
        private UUID verifiedBy;
        private OffsetDateTime verifiedAt;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
    }
}
