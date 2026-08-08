package com.finbank.auth_service.service;

import com.finbank.auth_service.dto.UserRequest;
import com.finbank.auth_service.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);
}