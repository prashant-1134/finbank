package com.finbank.auth_service.service;

import com.finbank.auth_service.entity.RefreshToken;
import com.finbank.auth_service.entity.User;


public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(String token);
}
