package com.legalaid.userauth.repository.admin;

import com.legalaid.userauth.entity.admin.AdminProfile;
import com.legalaid.userauth.entity.admin.AdminStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, UUID> {
    Optional<AdminProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    List<AdminProfile> findByStatus(AdminStatus status);
}
