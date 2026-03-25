package com.legalaid.userauth.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class ClientAddressRequest {
    @Data
    public static class AddressRegisterRequest {
        @NotBlank
        private String locationType;
        private String division;
        private String district;
        private String city;
        private String postalCode;
        private String street;
    }
}
