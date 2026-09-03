package com.finbank.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.finbank.auth_service.entity.PasswordResetToken;

import java.util.Optional;


public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long>{

    Optional<PasswordResetToken> findByToken(String token);

}
