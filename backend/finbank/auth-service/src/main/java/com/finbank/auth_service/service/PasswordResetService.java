package com.finbank.auth_service.service;

import com.finbank.auth_service.dto.ResetPasswordRequest;
import com.finbank.auth_service.dto.ForgotPasswordRequest;

public interface PasswordResetService {

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
