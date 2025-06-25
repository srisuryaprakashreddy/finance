package com.project.finance.repository;

import com.project.finance.model.Transactions;
import com.project.finance.model.User;
import com.project.finance.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByUser(User user);
    List<Transactions> findByAccount(Account account);
}
