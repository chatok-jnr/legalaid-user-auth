package com.legalaid.userauth.service;

import com.legalaid.userauth.dto.request.AuthRequests;
import com.legalaid.userauth.dto.response.AuthResponses;

import java.util.UUID;

public interface AuthService {

    AuthResponses.TokenResponse register(AuthRequests.RegisterRequest request);

    AuthResponses.TokenResponse login(AuthRequests.LoginRequest request);

    AuthResponses.TokenResponse refreshToken(AuthRequests.RefreshTokenRequest request);

    AuthResponses.UserResponse getProfile(String email);

    AuthResponses.UserResponse updateProfile(String email, AuthRequests.UpdateProfileRequest request);
}
