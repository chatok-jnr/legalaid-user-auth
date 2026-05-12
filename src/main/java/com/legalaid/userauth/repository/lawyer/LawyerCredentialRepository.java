package com.legalaid.userauth.repository.lawyer;

import com.legalaid.userauth.entity.lawyer.LawyerCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LawyerCredentialRepository extends JpaRepository<LawyerCredential, UUID> {
    List<LawyerCredential> findByLawyerId(UUID lawyerId);
}
