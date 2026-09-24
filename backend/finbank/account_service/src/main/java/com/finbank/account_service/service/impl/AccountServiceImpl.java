package com.finbank.account_service.service.impl;

import com.finbank.account_service.dto.*;
import com.finbank.account_service.entity.*;
import com.finbank.account_service.repository.*;
import com.finbank.account_service.service.*;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public AccountResponse deposit(String accountnumber, DepositRequest request){
        Account account = accountRepository.
                findByAccountNumber(accountnumber)
                .orElseThrow(()->
                        new RuntimeException("Account not found"));

        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account is not active");
        }

        account.setBalance(
                account.getBalance().add(request.getAmount())
        );

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
    public AccountResponse withdraw(String accountNumber, WithdrawRequest request){
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(
                        ()->new RuntimeException("Account Not found")
                );

        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account is not active");
        }

        /*Now we will check if we have sufficient amount to withdraw*/

        if(account.getBalance().compareTo(request.getAmount()) < 0){
            throw new RuntimeException("Amount is insufficient");

        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));

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
    @Transactional
    public AccountResponse transfer(TransferRequest request) {

        /*Check if to account and from account is not the same */
        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new RuntimeException("From and TO account can't be same");
        }

        /*check if from account exist*/

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() ->
                        new RuntimeException("Source account is not found"));

        /*check if destination account exist*/

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() ->
                        new RuntimeException("Dest account is not found"));

        /*Check if both the account's are active*/
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("From account is not active");
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("To account is not active");
        }

        // check if transfer amt is less than  balance

        if(fromAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new RuntimeException("Balance in the from account is not sufficient");
        }

        /*All condition are satisfied so process the balance transfer*/

        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));


        accountRepository.save(fromAccount);

        accountRepository.save(toAccount);

        return new AccountResponse(
                fromAccount.getId(),
                fromAccount.getAccountNumber(),
                fromAccount.getUserId(),
                fromAccount.getBalance(),
                fromAccount.getStatus()
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

