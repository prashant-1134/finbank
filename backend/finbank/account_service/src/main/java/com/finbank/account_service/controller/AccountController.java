package com.finbank.account_service.controller;

import com.finbank.account_service.dto.AccountResponse;
import com.finbank.account_service.dto.CreateAccountRequest;
import com.finbank.account_service.service.AccountService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.finbank.account_service.dto.DepositRequest;
import com.finbank.account_service.dto.WithdrawRequest;
import com.finbank.account_service.dto.TransferRequest;
import com.finbank.account_service.dto.TransactionResponse;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController{

    private final AccountService accountService;

    public AccountController(AccountService accountService){

        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ){
        AccountResponse response = accountService.createAccount(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber
    ){

        AccountResponse response = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request
    ){
        AccountResponse response = accountService.deposit(accountNumber, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody WithdrawRequest request
    ){
        AccountResponse response = accountService.withdraw(accountNumber, request);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/transfer")
    public ResponseEntity<AccountResponse> transfer(
            @Valid @RequestBody TransferRequest request
    ){
        AccountResponse response = accountService.transfer(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable String accountNumber
    ){
        List<TransactionResponse> transactions =
                accountService.getTransactionHistory(accountNumber);

        return ResponseEntity.ok(transactions);
    }
}