package com.legalaid.userauth.repository.lawyer.projection;

import java.util.List;

public interface LawyerProfileProjection {
    String getBarNumber();
    String getBio();
    List<String> getSpecializations();
    short getYearsExperience();
    Boolean getIsVerified();
    String getCredentials(); // JSON string of credentials
}
