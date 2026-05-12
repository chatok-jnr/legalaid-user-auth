package com.legalaid.userauth.service.lawyer;

import com.legalaid.userauth.dto.request.lawyer.LawyerCredentialRequest;
import com.legalaid.userauth.dto.request.lawyer.LawyerRequest;
import com.legalaid.userauth.dto.response.lawyer.LawyerCredentialResponse;
import com.legalaid.userauth.dto.response.lawyer.LawyerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LawyerService {
    LawyerResponse.LawyerProfileResponse registerLawyer(LawyerRequest.RegisterLawyer request, String email);
    LawyerResponse.LawyerProfileResponse updateLawyer(LawyerRequest.UpdateLawyer request, String email);
    LawyerResponse.LawyerProfileResponse getLawyerProfile(String email);
    LawyerResponse.DocumentUploadResponse uploadProfileDocument(MultipartFile document, String email);

    LawyerCredentialResponse.Create addLawyerCredential(LawyerCredentialRequest.Create request, String email);
}
