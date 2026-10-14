package com.finbank.account_service.service;
import com.finbank.account_service.dto.CreateAccountRequest;
import com.finbank.account_service.dto.AccountResponse;
import com.finbank.account_service.dto.DepositRequest;
import com.finbank.account_service.dto.WithdrawRequest;
import com.finbank.account_service.dto.TransferRequest;
import com.finbank.account_service.dto.TransactionResponse;

import java.util.List;

public interface AccountService{

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccount(String accoutNumber);

    AccountResponse deposit(String accountNumber, DepositRequest  request);

    AccountResponse withdraw(String accountNumber, WithdrawRequest request);

    AccountResponse transfer(TransferRequest request);

    List<TransactionResponse> getTransactionHistory(String accountNumber);
}