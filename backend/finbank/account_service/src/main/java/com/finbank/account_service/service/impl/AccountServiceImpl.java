package com.finbank.account_service.service.impl;

import com.finbank.account_service.dto.*;
import com.finbank.account_service.entity.*;
import com.finbank.account_service.repository.*;
import com.finbank.account_service.service.*;
import com.finbank.account_service.entity.Transaction;
import com.finbank.account_service.entity.TransactionType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;
import com.finbank.account_service.exception.AccountNotFoundException;
import com.finbank.account_service.exception.InsufficientBalanceException;
import com.finbank.account_service.exception.InactiveAccountException;
import com.finbank.account_service.exception.InvalidTransferException;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactioRepository){
        this.accountRepository = accountRepository;
        this.transactionRepository = transactioRepository;
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
                        new AccountNotFoundException("Account Not found")
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
    @Transactional
    public AccountResponse deposit(String accountnumber, DepositRequest request){
        Account account = accountRepository.
                findByAccountNumber(accountnumber)
                .orElseThrow(()->
                        new AccountNotFoundException("Account not found"));

        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new InactiveAccountException("Account is not active");
        }

        account.setBalance(
                account.getBalance().add(request.getAmount())
        );

        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAccountId(savedAccount.getId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setRelatedAccountNumber(null);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);


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
    public AccountResponse withdraw(String accountNumber, WithdrawRequest request){
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(
                        ()->new AccountNotFoundException("Account Not found")
                );

        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new InactiveAccountException("Account is not active");
        }

        /*Now we will check if we have sufficient amount to withdraw*/

        if(account.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance");

        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));

        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAccountId(savedAccount.getId());
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setRelatedAccountNumber(null);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);


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
            throw new InvalidTransferException("From and TO account can't be same");
        }

        /*check if from account exist*/

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() ->
                        new AccountNotFoundException("Source account is not found"));

        /*check if destination account exist*/

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() ->
                        new AccountNotFoundException("Dest account is not found"));

        /*Check if both the account's are active*/
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException("Src Account is not active");
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException("Dest Account is not active");
        }

        // check if transfer amt is less than  balance

        if(fromAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance in source account for transfer");
        }

        /*All condition are satisfied so process the balance transfer*/

        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));


        accountRepository.save(fromAccount);

        accountRepository.save(toAccount);

        /*Below code added for recording the trasaction history*/

        Transaction sentTransaction = new Transaction();

        sentTransaction.setAccountId(fromAccount.getId());
        sentTransaction.setType(TransactionType.TRANSFER_SENT);
        sentTransaction.setAmount(request.getAmount());
        sentTransaction.setRelatedAccountNumber(toAccount.getAccountNumber());
        sentTransaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(sentTransaction);

        // recieve transaction

        Transaction recTransaction = new Transaction();

        recTransaction.setAccountId(toAccount.getId());
        recTransaction.setType(TransactionType.TRNASFER_RECEIVE);
        recTransaction.setAmount(request.getAmount());
        recTransaction.setRelatedAccountNumber(fromAccount.getAccountNumber());
        recTransaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(recTransaction);

        return new AccountResponse(
                fromAccount.getId(),
                fromAccount.getAccountNumber(),
                fromAccount.getUserId(),
                fromAccount.getBalance(),
                fromAccount.getStatus()
        );
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(
            String accountNumber
    ) {
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
        .orElseThrow(() ->
                new AccountNotFoundException("Account Not found")
        );

        List<Transaction> transactions  =
                transactionRepository.findByAccountIdOrderByCreatedAtDesc(
                        account.getId()
                );

        return transactions.stream()
                .map((transaction) ->
                        new TransactionResponse(
                                transaction.getId(),
                                transaction.getType(),
                                transaction.getAmount(),
                                transaction.getRelatedAccountNumber(),
                                transaction.getCreatedAt()
                        )
                )
                .toList();
    }


    /*Helper method to generate the Account Number*/

    private String generateAccountNumber(){
        return UUID.randomUUID()
                .toString()
                .replace("-","")
                .substring(0,12);
    }
}

