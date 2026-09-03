package com.finbank.auth_service.service.impl;

import com.finbank.auth_service.dto.ForgotPasswordRequest;
import com.finbank.auth_service.dto.ResetPasswordRequest;
import com.finbank.auth_service.entity.PasswordResetToken;
import com.finbank.auth_service.entity.User;
import com.finbank.auth_service.exception.InvalidRefreshTokenException;
import com.finbank.auth_service.repository.PasswordResetTokenRepository;
import com.finbank.auth_service.repository.UserRepository;
import com.finbank.auth_service.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration}")
    private long resetTokenExpiration;

    public PasswordResetServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setToken(UUID.randomUUID().toString());

        resetToken.setUser(user);

        resetToken.setExpiryDate(
                LocalDateTime.now()
                        .plusSeconds(resetTokenExpiration / 1000)
        );

        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        // Temporary:
        // Later this token will be sent through Notification Service.
        System.out.println(
                "Password reset token: " + resetToken.getToken()
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(request.getToken())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid reset token")
                        );

        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Reset token has expired");
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        resetToken.setUsed(true);

        passwordResetTokenRepository.save(resetToken);
    }
}