package com.finbank.account_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.finbank.account_service.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}