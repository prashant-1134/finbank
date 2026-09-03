package com.finbank.auth_service.service;

import com.finbank.auth_service.dto.LoginRequest;
import com.finbank.auth_service.dto.LoginResponse;
import com.finbank.auth_service.dto.RefreshTokenRequest;
import com.finbank.auth_service.dto.RefreshTokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshAccessToken(
            RefreshTokenRequest request
    );

    void logout(String refreshToken);

    void logoutAll(String email);
}
