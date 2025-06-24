package com.project.finance.repository;

import com.project.finance.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByAccountUserUserId(Long userId);
}
