package com.finbank.account_service.dto;

import com.finbank.account_service.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse{

    private long id;
    private TransactionType  type;
    private BigDecimal amount;
    private String relatedAccountNumber;
    private LocalDateTime createdAt;
}