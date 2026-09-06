package com.finbank.account_service.dto;

import com.finbank.account_service.entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public class AccountResponse{
    private Long id;

    private String accoutNumber;

    private Long userId;

    private BigDecimal balance;

    private AccountStatus status;
}

