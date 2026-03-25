package com.legalaid.userauth.repository.lawyer;

import com.legalaid.userauth.entity.lawyer.LawyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface LawyerRepository extends JpaRepository<LawyerProfile, UUID> {
        Optional<LawyerProfile> findByBarNumber(String barNumber);

}
