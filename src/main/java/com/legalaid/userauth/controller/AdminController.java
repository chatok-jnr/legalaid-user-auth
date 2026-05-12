package com.legalaid.userauth.controller;

import com.legalaid.userauth.dto.request.admin.AdminRequest;
import com.legalaid.userauth.dto.response.admin.AdminResponse;
import com.legalaid.userauth.service.admin.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Creat new admin
    @PostMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponse.AdminProfileResponse> createAdminProfile(
            @Valid @RequestBody AdminRequest.RegisterAdminProfile request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createAdminProfile(request));
    }

    // Get my profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponse.AdminProfileResponse> getMyAdminProfile(Authentication auth) {
        return ResponseEntity.ok(adminService.getMyAdminProfile(auth.getName()));
    }

    // Get other admin profile by id
    @GetMapping("/profiles/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponse.AdminProfileResponse> getAdminProfile(@PathVariable UUID adminId) {
        return ResponseEntity.ok(adminService.getAdminProfile(adminId));
    }


    @PatchMapping("/profiles/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponse.AdminProfileResponse> updateAdminStatus(
            @Valid @RequestBody AdminRequest.UpdateAdminStatus request
    ) {
        return ResponseEntity.ok(adminService.updateAdminStatus(request));
    }

    @PostMapping("/actions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponse.AdminActionLogResponse> createActionLog(
            @Valid @RequestBody AdminRequest.CreateActionLog request,
            Authentication auth,
            HttpServletRequest servletRequest
    ) {
        String userAgent = servletRequest.getHeader("User-Agent");
        if (userAgent == null || userAgent.isBlank()) {
            userAgent = "Unknown";
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createActionLog(
                        request,
                        auth.getName(),
                        servletRequest.getRemoteAddr(),
                        userAgent
                ));
    }

    @GetMapping("/actions/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminResponse.AdminActionLogResponse>> getMyActionLogs(Authentication auth) {
        return ResponseEntity.ok(adminService.getMyActionLogs(auth.getName()));
    }

    @GetMapping("/actions/target/{targetId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminResponse.AdminActionLogResponse>> getActionLogsByTarget(@PathVariable UUID targetId) {
        return ResponseEntity.ok(adminService.getActionLogsByTarget(targetId));
    }
}
