package com.legalaid.userauth.dto.request.lawyer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

public class LawyerRequest {

    @Data
    public static class RegisterLawyer{
        @NotNull(message = "User id is required")
        private UUID id;

        @NotBlank
        private String barNumber;

        private String bio;
        private List<String> specializations;
        private short yearsExperience;

        @Min(100)
        private BigDecimal consultationFee;
    }

    @Data
    public static class UpdateLawyer{
        private String barNumber;
        private String bio;
        private List<String> specializations;
        private short yearsExperience;
        private BigDecimal consultationFee;
    }
}
