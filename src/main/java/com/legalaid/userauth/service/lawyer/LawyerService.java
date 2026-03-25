package com.legalaid.userauth.service.lawyer;

import com.legalaid.userauth.dto.request.lawyer.LawyerRequest;
import com.legalaid.userauth.dto.response.lawyer.LawyerResponse;

public interface LawyerService {
    LawyerResponse.LawyerProfileResponse registerLawyer(LawyerRequest.RegisterLawyer request, String email);
    LawyerResponse.LawyerProfileResponse updateLawyer(LawyerRequest.UpdateLawyer request, String email);
    LawyerResponse.LawyerProfileResponse getLawyerProfile(String email);
}
