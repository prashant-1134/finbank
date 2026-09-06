package com.finbank.account_service.repository;
import com.finbank.account_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>{

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findById(Long userId);
}

