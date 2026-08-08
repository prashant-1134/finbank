package com.finbank.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse{

    private Long id;

    private String fullName;

    private String email;

}