package com.finbank.account_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest{
    @NotNull(message = "source accountNumber can't be null")
    private String fromAccount;

    @NotNull(message = "destination account number can't be null")
    private String toAccount;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than zero")
    private BigDecimal amount;
}