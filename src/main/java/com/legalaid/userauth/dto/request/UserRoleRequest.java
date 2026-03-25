package com.legalaid.userauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;

public class UserRoleRequest {

    @Data
    public static class AssignRoleRequest {
        @NotNull
        private short roleId;
        @NotNull
        private UUID userId;
    }
}
