package com.legalaid.userauth.controller;

import com.legalaid.userauth.dto.request.lawyer.LawyerRequest;
import com.legalaid.userauth.dto.response.lawyer.LawyerResponse;
import com.legalaid.userauth.service.client.ClientService;
import com.legalaid.userauth.service.lawyer.LawyerService;
import com.legalaid.userauth.service.lawyer.impl.LawyerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/lawyer")
@RequiredArgsConstructor
public class LawyerController {
    private final LawyerService lawyerService;

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LawyerResponse.LawyerProfileResponse> registerLawyer(
            @AuthenticationPrincipal UserDetails principal,
            @Valid
            @RequestBody
            LawyerRequest.RegisterLawyer request,
            Authentication auth
            ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lawyerService.registerLawyer(request, auth.getName()));
    }

    @PatchMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LawyerResponse.LawyerProfileResponse> updateLawyer(
            @AuthenticationPrincipal UserDetails principal,
            @Valid
            @RequestBody
            LawyerRequest.UpdateLawyer request,
            Authentication auth
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lawyerService.updateLawyer(request, auth.getName()));
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LawyerResponse.LawyerProfileResponse> getLawyer(
            @AuthenticationPrincipal UserDetails principal,
            Authentication auth
            ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lawyerService.getLawyerProfile(auth.getName()));
    }
}
