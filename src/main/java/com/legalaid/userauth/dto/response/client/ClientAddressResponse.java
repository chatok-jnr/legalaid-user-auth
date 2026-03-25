package com.legalaid.userauth.dto.response.client;


import lombok.Builder;
import lombok.Data;

public class ClientAddressResponse {
    @Data
    @Builder
    public static class ClientAddress{
        private String locationType;

        private String division;
        private String district;
        private String city;
        private String postalCode;
        private String street;

        private boolean isVisible;
    }
}
