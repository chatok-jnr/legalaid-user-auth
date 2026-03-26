package com.legalaid.userauth.controller;

import com.legalaid.userauth.dto.request.UserAddressRequest;
import com.legalaid.userauth.dto.response.UserAddressResponse;
import com.legalaid.userauth.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final UserAddressService userAddressService;

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse.Address> registerAddress(
            @AuthenticationPrincipal UserDetails principal,
            @Valid
            @RequestBody
            UserAddressRequest.RegisterUserAddress request,
            Authentication auth
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAddressService.registerAddress(request, auth.getName()));
    }

    @PatchMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse.Address> updateAddress(
            @AuthenticationPrincipal UserDetails principal,
            @Valid
            @RequestBody
            UserAddressRequest.UpdateUserAddress request,
            Authentication auth
    ) {
        return ResponseEntity
                .status(200)
                .body(userAddressService.updateAddress(request, auth.getName()));
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserAddressResponse.Address>> getAddress(
            @AuthenticationPrincipal UserDetails principal,
            Authentication auth
    ) {
        return ResponseEntity
                .status(200)
                .body(userAddressService.getAddress(auth.getName()));
    }
}
