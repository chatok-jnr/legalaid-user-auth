package com.legalaid.userauth.dto.response;

import lombok.Builder;
import lombok.Data;

public class UserAddressResponse {

    @Builder
    @Data
    public static class Address {
        private String addressType;
        private String division;
        private String district;
        private String city;
        private String postalCode;
        private String street;
    }
}
