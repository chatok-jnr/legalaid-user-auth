package com.legalaid.userauth.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.*;

public class UserAddressRequest {

    @Data
    public static class RegisterUserAddress {
        private UUID user_id;
        private String addressType;

        private String division;
        private String district;
        private String city;
        private String postalCode;
        private String street;
    }

    @Data
    public static class UpdateUserAddress {
        private String addressType;
        private String division;
        private String district;
        private String city;
        private String postalCode;
        private String street;
    }
}
