package com.legalaid.userauth.controller;

import com.legalaid.userauth.dto.request.lawyer.LawyerCredentialRequest;
import com.legalaid.userauth.dto.request.lawyer.LawyerRequest;
import com.legalaid.userauth.dto.response.lawyer.LawyerCredentialResponse;
import com.legalaid.userauth.dto.response.lawyer.LawyerResponse;
import com.legalaid.userauth.entity.lawyer.LawyerStatus;
import com.legalaid.userauth.service.lawyer.LawyerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("auth/lawyer")
@RequiredArgsConstructor
public class LawyerController {
    private final LawyerService lawyerService;

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LawyerResponse.LawyerProfileResponse> registerLawyer(
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
            Authentication auth
            ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lawyerService.getLawyerProfile(auth.getName()));
    }

    @PostMapping(value = "/profile/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LawyerResponse.DocumentUploadResponse> uploadProfileDocument(
            @RequestPart("document") MultipartFile document,
            Authentication auth
    ) {
        return ResponseEntity.ok(lawyerService.uploadProfileDocument(document, auth.getName()));
    }

    @PostMapping("/credentials")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LawyerCredentialResponse.Create> addLawyerCredential(
            @Valid
            @RequestBody
            LawyerCredentialRequest.Create request,
            Authentication auth
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lawyerService.addLawyerCredential(request, auth.getName()));
    }

    // 4 Admins ------------------------------------
    @GetMapping("/admin/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LawyerResponse.LawyerDetailsForAdmin>> getAllLawyerForAdmin(
            @PathVariable String status
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lawyerService.getAllLawyerForAdmin(LawyerStatus.valueOf(status.toUpperCase())));
    }

    @PatchMapping("/admin/{lawyerId}/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateLawyerStatus(
            @PathVariable String lawyerId,
            @PathVariable String status,
            Authentication auth
    ) {
        lawyerService.updLawyerStatus(java.util.UUID.fromString(lawyerId), status, auth.getName());
    }
}
