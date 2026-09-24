package com.finbank.account_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
public class WithdrawRequest{
    @NotNull(message = "Amount should not be null")
    @DecimalMin(value = "0.01", message = "Amount can't be negative")
    private BigDecimal  amount;

}
