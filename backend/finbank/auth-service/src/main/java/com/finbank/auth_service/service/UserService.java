package com.finbank.auth_service.service;

import com.finbank.auth_service.dto.ProfileUpdateRequest;
import com.finbank.auth_service.dto.UserRequest;
import com.finbank.auth_service.dto.UserResponse;
import com.finbank.auth_service.dto.ProfileUpdateRequest;
import com.finbank.auth_service.dto.ChangePasswordRequest;

public interface UserService {
    UserResponse registerUser(UserRequest request);

    UserResponse getCurrentUser(String email);

    UserResponse updateProfile(
            String currEmail,
            ProfileUpdateRequest request
    );

    void changePassword(
            String currentEmail,
            ChangePasswordRequest requesst
    );
}