package com.legalaid.userauth.repository;

import com.legalaid.userauth.entity.UserRole;
import com.legalaid.userauth.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUserId(UUID userId);
}
