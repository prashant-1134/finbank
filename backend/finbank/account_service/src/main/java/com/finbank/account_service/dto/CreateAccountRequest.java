package com.finbank.account_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest{

    @NotNull(message = "UserId should be present")
    private Long userId;
}