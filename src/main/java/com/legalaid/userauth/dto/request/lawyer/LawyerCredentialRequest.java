package com.legalaid.userauth.dto.request.lawyer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.UUID;

public class LawyerCredentialRequest {

    @Data
    public static class Create{
        @NotNull
        private UUID lawyerId;

        @NotNull
        @Length(max = 50)
        private String credentialType;

        @Length(max = 200)
        private String title;

        @Length(max = 200)
        private String issuingBody;
        private LocalDate issuedDate; // ISO format: YYYY-MM-DD
        private LocalDate expiryDate; // ISO format: YYYY-MM-DD

        @NotNull
        private String documentUrl;
    }
}
