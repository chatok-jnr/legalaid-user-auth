package com.legalaid.userauth.service.lawyer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legalaid.userauth.dto.request.lawyer.LawyerCredentialRequest;
import com.legalaid.userauth.dto.request.lawyer.LawyerRequest;
import com.legalaid.userauth.dto.response.lawyer.LawyerCredentialResponse;
import com.legalaid.userauth.dto.response.lawyer.LawyerResponse;
import com.legalaid.userauth.entity.Role;
import com.legalaid.userauth.entity.User;
import com.legalaid.userauth.entity.UserRole;
import com.legalaid.userauth.entity.UserRoleId;
import com.legalaid.userauth.entity.lawyer.LawyerCredential;
import com.legalaid.userauth.entity.lawyer.LawyerProfile;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.RoleRepository;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.repository.UserRoleRepository;
import com.legalaid.userauth.repository.lawyer.LawyerCredentialRepository;
import com.legalaid.userauth.repository.lawyer.LawyerRepository;
import com.legalaid.userauth.repository.lawyer.projection.LawyerProfileProjection;
import com.legalaid.userauth.service.cloudinary.CloudinaryService;
import com.legalaid.userauth.service.lawyer.LawyerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LawyerServiceImpl implements LawyerService {

    private final LawyerRepository      lawyerRepository;
    private final LawyerCredentialRepository lawyerCredentialRepository;
    private final UserRepository        userRepository;
    private final RoleRepository        roleRepository;
    private final UserRoleRepository    userRoleRepository;
    private final CloudinaryService     cloudinaryService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public LawyerResponse.LawyerProfileResponse registerLawyer(LawyerRequest.RegisterLawyer request, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("User with ID " + request.getId() + "not found"));

        if(!request.getId().equals(user.getId())) {
            throw new AuthExceptions.UnauthorizedException();
        }

        if(lawyerRepository.existsById(request.getId())) {
            throw new AuthExceptions.LawyerAlreadyExistException();
        }

        LawyerProfile lawyerProfile = LawyerProfile.builder()
                .user(user)
                .barNumber(request.getBarNumber())
                .bio(request.getBio())
                .specializations(request.getSpecializations())
                .consultationFee(request.getConsultationFee())
                .yearsExperience(request.getYearsExperience())
                .build();


        Role role = roleRepository.findByName(Role.RoleName.LAWYER)
                .orElseThrow(AuthExceptions.RoleNotFoundException::new);

        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());

        UserRole userRole = UserRole.builder()
                        .id(userRoleId)
                        .role(role)
                        .user(user)
                        .build();

        userRoleRepository.save(userRole);
        lawyerRepository.save(lawyerProfile);

        return buildProfileResponse(lawyerProfile);
    }

    @Override
    @Transactional
    public LawyerResponse.LawyerProfileResponse updateLawyer(LawyerRequest.UpdateLawyer request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("User not found"));
        LawyerProfile lawyerProfile = lawyerRepository.findById(user.getId())
                .orElseThrow(AuthExceptions.LawyerNotFoundException::new);

        if(request.getBarNumber() != null) lawyerProfile.setBarNumber(request.getBarNumber());
        if(request.getBio() != null) lawyerProfile.setBio(request.getBio());
        if(request.getSpecializations() != null) lawyerProfile.setSpecializations(request.getSpecializations());
        if(request.getYearsExperience() != lawyerProfile.getYearsExperience()) lawyerProfile.setYearsExperience(request.getYearsExperience());
        if(request.getConsultationFee() != null) lawyerProfile.setConsultationFee(request.getConsultationFee());

        lawyerRepository.save(lawyerProfile);
        return buildProfileResponse(lawyerProfile);
    }

    @Override
    public LawyerResponse.LawyerProfileResponse getLawyerProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("User not found"));

        LawyerProfileProjection lawyerProfile = lawyerRepository.findLawyerProfileById(user.getId());

        return LawyerResponse.LawyerProfileResponse.builder()
                .barNumber(lawyerProfile.getBarNumber())
                .bio(lawyerProfile.getBio())
                .specializations(lawyerProfile.getSpecializations())
                .yearsExperience(lawyerProfile.getYearsExperience())
                .isVerified(lawyerProfile.getIsVerified())
                .credentials(parseDocuments(lawyerProfile.getCredentials()))
                .build();
    }

    @Override
    public LawyerResponse.DocumentUploadResponse uploadProfileDocument(MultipartFile document, String email) {
        if (document == null || document.isEmpty()) {
            throw new AuthExceptions.InvalidUploadException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("User not found"));

        String documentUrl = cloudinaryService.uploadDocument(document);
        return LawyerResponse.DocumentUploadResponse.builder()
                .documentUrl(documentUrl)
                .build();
    }

    @Override
    @Transactional
    public LawyerCredentialResponse.Create addLawyerCredential(LawyerCredentialRequest.Create request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("User not found"));

        LawyerProfile lawyerProfile = lawyerRepository.findById(user.getId())
                .orElseThrow(AuthExceptions.LawyerNotFoundException::new);

        LawyerCredential lawyerCredential = lawyerCredentialRepository.save(
                LawyerCredential.builder()
                        .lawyer(lawyerProfile)
                        .credentialType(request.getCredentialType())
                        .title(request.getTitle())
                        .issuingBody(request.getIssuingBody())
                        .issuedDate(request.getIssuedDate())
                        .expiryDate(request.getExpiryDate())
                        .documentUrl(request.getDocumentUrl())
                        .createdAt(Instant.now())
                        .build()
        );

        System.out.println("Saved Lawyer Credential: " + lawyerCredential.getId() + " for Lawyer: " + lawyerProfile.getId());

        return LawyerCredentialResponse.Create.builder()
                .id(lawyerCredential.getId())
                .lawyerId(lawyerProfile.getId())
                .title(lawyerCredential.getTitle())
                .issuingBody(lawyerCredential.getIssuingBody())
                .issueDate(lawyerCredential.getIssuedDate())
                .expiryDate(lawyerCredential.getExpiryDate())
                .documentUrl(lawyerCredential.getDocumentUrl())
                .credentialType(lawyerCredential.getCredentialType())
                .createdAt(lawyerCredential.getCreatedAt())
                .build();
    }


    // ===================================
    // Functions
    // ===================================

    private LawyerResponse.LawyerProfileResponse buildProfileResponse(LawyerProfile request) {
        return LawyerResponse.LawyerProfileResponse.builder()
                .barNumber(request.getBarNumber())
                .bio(request.getBio())
                .specializations(request.getSpecializations())
                .yearsExperience(request.getYearsExperience())
                .isVerified(request.isVerified())
                .build();
    }

    private List<LawyerResponse.Document> parseDocuments(String credentials) {
        try {
            return (credentials == null || credentials.isBlank())
                    ? List.of()
                    : objectMapper.readValue(credentials, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Error parsing delivery files JSON: {}", e.getMessage());
            return List.of();
        }
    }
}





