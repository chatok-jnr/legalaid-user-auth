package com.legalaid.userauth.controller;

import com.legalaid.userauth.dto.request.client.ClientProfileRequest;
import com.legalaid.userauth.dto.response.client.ClientProfileResponse;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.service.client.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final UserRepository userRepository;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientProfileResponse.ProfileResponse> registerProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ClientProfileRequest.RegisterRequest request
    ) {
        UUID userId = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(principal.getUsername()))
                .getId();

        request.setUserId(userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clientService.registerRequest(request));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientProfileResponse.ProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails principal
    ) {
        UUID userId = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(principal.getUsername()))
                .getId();

        return ResponseEntity.ok(clientService.getClient(userId));
    }

    @PatchMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientProfileResponse.ProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ClientProfileRequest.UpdateRequest request
    ) {
        UUID userId = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(principal.getUsername()))
                .getId();

        request.setUserId(userId);
        return ResponseEntity.ok(clientService.updateClient(request));
    }
}
