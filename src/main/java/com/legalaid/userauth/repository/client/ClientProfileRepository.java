package com.legalaid.userauth.repository.client;

import com.legalaid.userauth.entity.client.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {
    Optional<ClientProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
