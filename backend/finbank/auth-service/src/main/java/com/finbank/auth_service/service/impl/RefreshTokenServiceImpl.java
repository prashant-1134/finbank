package com.finbank.auth_service.service.impl;

import com.finbank.auth_service.entity.RefreshToken;
import com.finbank.auth_service.entity.User;
import com.finbank.auth_service.repository.RefreshTokenRepository;
import com.finbank.auth_service.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finbank.auth_service.exception.InvalidRefreshTokenException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken  = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken.setUser(user);

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusSeconds(refreshExpiration / 1000)
        );

        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()->
                        new InvalidRefreshTokenException("Refresh Token not Found")
                );

        if(refreshToken.isRevoked()){
            throw new InvalidRefreshTokenException(
                    "Refresh token has been revoked"
            );
        }

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new InvalidRefreshTokenException("Refresh token has been expired");
        }

        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException("Refresh token not found"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

        @Override
        @Transactional
        public void revokeAllRefreshToken(Long userId){
            refreshTokenRepository.revokeAllByUserId(userId);
        }
}
