package com.legalaid.userauth.service.client.impl;

import com.legalaid.userauth.dto.request.client.ClientProfileRequest;
import com.legalaid.userauth.dto.response.client.ClientProfileResponse;
import com.legalaid.userauth.entity.User;
import com.legalaid.userauth.entity.client.ClientProfile;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.repository.client.ClientProfileRepository;
import com.legalaid.userauth.service.client.ClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientProfileRepository clientRepository;
    private final UserRepository userRepository;
    // Register a new client Profile
    @Override
    @Transactional
    public ClientProfileResponse.ProfileResponse registerRequest(ClientProfileRequest.RegisterRequest request) {
        if(clientRepository.existsByUserId(request.getUserId())) {
            throw new AuthExceptions.ClientAlreadyExistException();
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("User with ID " + request.getUserId() + " not found"));
        ClientProfile clientProfile = ClientProfile.builder()
                .user(user) // Only set user, not userId, for @MapsId
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .notes(request.getNotes())
                .build();

        clientRepository.save(clientProfile);
        return buildProfileResponse(clientProfile);
    }

    @Override
    public ClientProfileResponse.ProfileResponse getClient(UUID clientId) {
        ClientProfile clientProfile = clientRepository.findByUserId(clientId)
                .orElseThrow(() -> new AuthExceptions.ClientNotFoundException());
        return buildProfileResponse(clientProfile);
    }

    @Override
    @Transactional
    public ClientProfileResponse.ProfileResponse updateClient(ClientProfileRequest.UpdateRequest request) {
        ClientProfile clientProfile = clientRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AuthExceptions.ClientNotFoundException());

        // Defensive: ensure user is set (for @MapsId)
        if (clientProfile.getUser() == null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AuthExceptions.UserNotFoundException("User with ID " + request.getUserId() + " not found"));
            clientProfile.setUser(user);
        }

        if(request.getEmergencyContactName()    != null) clientProfile.setEmergencyContactName(request.getEmergencyContactName());
        if(request.getEmergencyContactPhone()   != null) clientProfile.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if(request.getNotes()                   != null ) clientProfile.setNotes(request.getNotes());

        clientRepository.save(clientProfile);
        return buildProfileResponse(clientProfile);
    }

    private ClientProfileResponse.ProfileResponse buildProfileResponse(ClientProfile request) {
        return ClientProfileResponse.ProfileResponse.builder()
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .notes(request.getNotes())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
