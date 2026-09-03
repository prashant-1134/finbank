package com.finbank.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Current Password is Required")
    private String currentPassword;

    @NotBlank(message = "New Password is Required")
    @Size(min= 8, message = "New Password must be atleas 8 characters")
    private String newPassword;

}
