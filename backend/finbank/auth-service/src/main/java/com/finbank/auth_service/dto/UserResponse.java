package com.finbank.auth_service.dto;

import lombok.Getter;
import lombok.Setter;
import com.finbank.auth_service.entity.Role;


@Getter
@Setter
public class UserResponse{

    private Long id;

    private String fullName;

    private String email;

    private Role role;

}