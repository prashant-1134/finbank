package com.finbank.account_service.service.impl;

import com.finbank.account_service.dto.*;
import com.finbank.account_service.entity.*;
import com.finbank.account_service.repository.*;
import com.finbank.account_service.service.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request){
        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());

        account.setUserId(request.getUserId());

        account.setBalance(BigDecimal.ZERO);

        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        return new AccountResponse(
                savedAccount.getId(),
                savedAccount.getAccountNumber(),
                savedAccount.getUserId(),
                savedAccount.getBalance(),
                savedAccount.getStatus()
        );
    }

    @Override
    public AccountResponse getAccount(String accountNumber){
         Account account = accountRepository.
                 findByAccountNumber(accountNumber)
                .orElseThrow(()->
                        new RuntimeException("Account Not found")
                );

         return new AccountResponse(
                 account.getId(),
                 account.getAccountNumber(),
                 account.getUserId(),
                 account.getBalance(),
                 account.getStatus()
         );


    }

    /*Helper method to generate the Account Number*/

    private String generateAccountNumber(){
        return UUID.randomUUID()
                .toString()
                .replace("-","")
                .substring(0,12);
    }
}

