package com.finbank.auth_service.service;

import com.finbank.auth_service.dto.LoginRequest;
import com.finbank.auth_service.dto.LoginResponse;


public interface AuthService {
    LoginResponse login(LoginRequest request);
}
