package com.legalaid.userauth.repository.admin;

import com.legalaid.userauth.entity.admin.AdminActionLog;
import com.legalaid.userauth.entity.admin.AdminActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, UUID> {
    List<AdminActionLog> findByActionType(AdminActionType actionType);
    List<AdminActionLog> findByAdminId(UUID adminId);
    List<AdminActionLog> findByTargetId(UUID targetId);
}
