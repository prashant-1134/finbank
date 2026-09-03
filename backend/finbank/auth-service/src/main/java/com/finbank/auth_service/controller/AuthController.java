package com.finbank.auth_service.controller;

import com.finbank.auth_service.dto.*;
import com.finbank.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.finbank.auth_service.service.PasswordResetService;
import com.finbank.auth_service.dto.ForgotPasswordRequest;
import com.finbank.auth_service.dto.ResetPasswordRequest;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;


    public AuthController(AuthService authService,
                          PasswordResetService passwordResetService){
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ){
        LoginResponse response  = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                authService.refreshAccessToken(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshTokenRequest request
    ){
        authService.logout(request.getRefreshToken());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            Principal principal
    ){
        authService.logoutAll(principal.getName());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody
            ForgotPasswordRequest request
    ){
        passwordResetService.forgotPassword(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody
            ResetPasswordRequest request
    ){
        passwordResetService.resetPassword(request);

        return ResponseEntity.noContent().build();
    }
}
