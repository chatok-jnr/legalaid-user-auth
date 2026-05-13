package com.legalaid.userauth.service.admin.impl;

import com.legalaid.userauth.dto.request.admin.AdminRequest;
import com.legalaid.userauth.dto.response.admin.AdminResponse;
import com.legalaid.userauth.entity.Role;
import com.legalaid.userauth.entity.User;
import com.legalaid.userauth.entity.UserRole;
import com.legalaid.userauth.entity.UserRoleId;
import com.legalaid.userauth.entity.admin.AdminActionLog;
import com.legalaid.userauth.entity.admin.AdminProfile;
import com.legalaid.userauth.entity.admin.AdminStatus;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.RoleRepository;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.repository.UserRoleRepository;
import com.legalaid.userauth.repository.admin.AdminActionLogRepository;
import com.legalaid.userauth.repository.admin.AdminProfileRepository;
import com.legalaid.userauth.service.admin.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminProfileRepository adminProfileRepository;
    private final AdminActionLogRepository adminActionLogRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public AdminResponse.AdminProfileResponse createAdminProfile(AdminRequest.RegisterAdminProfile request) {
        if (adminProfileRepository.existsByUserId(request.getUserId())) {
            throw new AuthExceptions.AdminAlreadyExistException();
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(request.getUserId().toString()));

        AdminProfile refBy = null;

        if (request.getRefBy() != null) {
            refBy = adminProfileRepository.findById(request.getRefBy())
                    .orElseThrow(AuthExceptions.AdminNotFoundException::new);
        }

        AdminProfile adminProfile = AdminProfile.builder()
                .user(user)
                .refBy(refBy.getId())
                .status(request.getStatus() == null ? AdminStatus.PENDING : request.getStatus())
                .build();

        assignAdminRole(user);
        adminProfileRepository.save(adminProfile);

        return buildProfileResponse(adminProfile);
    }

    @Override
    @Transactional
    public AdminResponse.AdminProfileResponse getAdminProfile(UUID adminId) {
        AdminProfile adminProfile = adminProfileRepository.findById(adminId)
                .orElseThrow(AuthExceptions.AdminNotFoundException::new);

        return buildProfileResponse(adminProfile);
    }

    @Override
    @Transactional
    public AdminResponse.AdminProfileResponse getMyAdminProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(email));

        AdminProfile adminProfile = adminProfileRepository.findById(user.getId())
                .orElseThrow(AuthExceptions.AdminNotFoundException::new);

        return buildProfileResponse(adminProfile);
    }

    @Override
    @Transactional
    public AdminResponse.AdminProfileResponse updateAdminStatus(AdminRequest.UpdateAdminStatus request) {
        AdminProfile adminProfile = adminProfileRepository.findById(request.getUserId())
                .orElseThrow(AuthExceptions.AdminNotFoundException::new);

        adminProfile.setStatus(request.getStatus());
        adminProfileRepository.save(adminProfile);

        return buildProfileResponse(adminProfile);
    }

    @Override
    @Transactional
    public AdminResponse.AdminActionLogResponse createActionLog(
            AdminRequest.CreateActionLog request,
            String adminEmail,
            String ipAddress,
            String userAgent
    ) {
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(adminEmail));

        AdminProfile adminProfile = adminProfileRepository.findById(adminUser.getId())
                .orElseThrow(AuthExceptions.AdminNotFoundException::new);

        AdminActionLog actionLog = AdminActionLog.builder()
                .actionType(request.getActionType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .admin(adminProfile)
                .ipAddress(parseIpAddress(ipAddress))
                .userAgent(userAgent)
                .build();

        adminActionLogRepository.save(actionLog);

        return buildActionLogResponse(actionLog);
    }

    @Override
    @Transactional
    public List<AdminResponse.AdminActionLogResponse> getMyActionLogs(String adminEmail) {
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(adminEmail));

        return adminActionLogRepository.findByAdminId(adminUser.getId()).stream()
                .map(this::buildActionLogResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<AdminResponse.AdminActionLogResponse> getActionLogsByTarget(UUID targetId) {
        return adminActionLogRepository.findByTargetId(targetId).stream()
                .map(this::buildActionLogResponse)
                .toList();
    }

    private void assignAdminRole(User user) {
        Role role = roleRepository.findByName(Role.RoleName.ADMIN)
                .orElseThrow(AuthExceptions.RoleNotFoundException::new);

        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());
        if (userRoleRepository.existsById(userRoleId)) {
            return;
        }

        UserRole userRole = UserRole.builder()
                .id(userRoleId)
                .role(role)
                .user(user)
                .build();

        userRoleRepository.save(userRole);
    }

    private InetAddress parseIpAddress(String ipAddress) {
        try {
            return InetAddress.getByName(ipAddress);
        } catch (UnknownHostException ex) {
            throw new AuthExceptions.InvalidIpAddressException(ipAddress);
        }
    }

    private AdminResponse.AdminProfileResponse buildProfileResponse(AdminProfile adminProfile) {
        return AdminResponse.AdminProfileResponse.builder()
                .id(adminProfile.getId())
                .refById(adminProfile.getRefBy())
                .status(adminProfile.getStatus())
                .createdAt(adminProfile.getCreatedAt())
                .updatedAt(adminProfile.getUpdatedAt())
                .build();
    }

    private AdminResponse.AdminActionLogResponse buildActionLogResponse(AdminActionLog actionLog) {
        return AdminResponse.AdminActionLogResponse.builder()
                .id(actionLog.getId())
                .actionType(actionLog.getActionType())
                .targetId(actionLog.getTargetId())
                .reason(actionLog.getReason())
                .adminId(actionLog.getAdmin().getId())
                .ipAddress(actionLog.getIpAddress().getHostAddress())
                .userAgent(actionLog.getUserAgent())
                .createdAt(actionLog.getCreatedAt())
                .updatedAt(actionLog.getUpdatedAt())
                .build();
    }
}
