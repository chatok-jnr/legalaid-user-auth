package com.legalaid.userauth.service.admin;

import com.legalaid.userauth.dto.request.admin.AdminRequest;
import com.legalaid.userauth.dto.response.admin.AdminResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    AdminResponse.AdminProfileResponse createAdminProfile(AdminRequest.RegisterAdminProfile request);
    AdminResponse.AdminProfileResponse getAdminProfile(UUID adminId);
    AdminResponse.AdminProfileResponse getMyAdminProfile(String email);
    AdminResponse.AdminProfileResponse updateAdminStatus(AdminRequest.UpdateAdminStatus request);
    AdminResponse.AdminActionLogResponse createActionLog(
            AdminRequest.CreateActionLog request,
            String adminEmail,
            String ipAddress,
            String userAgent
    );
    List<AdminResponse.AdminActionLogResponse> getMyActionLogs(String adminEmail);
    List<AdminResponse.AdminActionLogResponse> getActionLogsByTarget(UUID targetId);
}
