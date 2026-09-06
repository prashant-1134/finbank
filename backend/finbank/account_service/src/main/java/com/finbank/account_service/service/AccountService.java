package com.finbank.account_service.service;
import com.finbank.account_service.dto.CreateAccountRequest;
import com.finbank.account_service.dto.AccountResponse;

public interface AccountService{

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccount(String accoutNumber);
}