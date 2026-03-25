package com.legalaid.userauth.dto.response;

import lombok.Data;

public class UserRoleResponse {

    @Data
    public static class AssignRoleResponse {
        private short roleId;
    }
}
