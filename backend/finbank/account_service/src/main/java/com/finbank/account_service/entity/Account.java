package com.finbank.account_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.finbank.account_service.entity.AccountStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

}