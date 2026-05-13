package com.legalaid.userauth.repository.lawyer.projection;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface LawyerDetailsProjectionForAdmin {
    UUID getId();
    String getProfilePicUrl();
    String getFullName();
    String getEmail();
    List<String> getSpecializations();
    short getExperience();
    Instant getApplied();
    Boolean getIsVerified();
    Instant getVerifiedAt();
    UUID getVerifiedBy();
    String getDocuments();
}
