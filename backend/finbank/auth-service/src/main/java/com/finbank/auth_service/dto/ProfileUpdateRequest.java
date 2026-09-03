package com.finbank.auth_service.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ProfileUpdateRequest {

    @NotBlank(message= "fullName is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "email must be valid")
    private String email;
}
