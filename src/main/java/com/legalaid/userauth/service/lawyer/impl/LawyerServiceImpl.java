package com.legalaid.userauth.service.lawyer.impl;

import com.legalaid.userauth.dto.request.lawyer.LawyerRequest;
import com.legalaid.userauth.dto.response.lawyer.LawyerResponse;
import com.legalaid.userauth.entity.Role;
import com.legalaid.userauth.entity.User;
import com.legalaid.userauth.entity.UserRole;
import com.legalaid.userauth.entity.UserRoleId;
import com.legalaid.userauth.entity.lawyer.LawyerProfile;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.RoleRepository;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.repository.UserRoleRepository;
import com.legalaid.userauth.repository.lawyer.LawyerRepository;
import com.legalaid.userauth.service.lawyer.LawyerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LawyerServiceImpl implements LawyerService {

    private final LawyerRepository      lawyerRepository;
    private final UserRepository        userRepository;
    private final RoleRepository        roleRepository;
    private final UserRoleRepository    userRoleRepository;

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
                .orElseThrow(() -> new AuthExceptions.RoleNotFoundException());

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
                .orElseThrow(() -> new AuthExceptions.LawyerNotFoundException());

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
        LawyerProfile lawyerProfile = lawyerRepository.findById(user.getId())
                .orElseThrow(() -> new AuthExceptions.LawyerNotFoundException());
        return buildProfileResponse(lawyerProfile);
    }

    private LawyerResponse.LawyerProfileResponse buildProfileResponse(LawyerProfile request) {
        return LawyerResponse.LawyerProfileResponse.builder()
                .barNumber(request.getBarNumber())
                .bio(request.getBio())
                .specializations(request.getSpecializations())
                .consultationFee(request.getConsultationFee())
                .yearsExperience(request.getYearsExperience())
                .isVerified(request.isVerified())
                .verifiedBy(request.getVerifiedBy())
                .verifiedAt(request.getVerifiedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
