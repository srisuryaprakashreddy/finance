package com.project.finance.repository;

import com.project.finance.model.Transfer;
import com.project.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findBySender(User sender);
    List<Transfer> findByReceiver(User receiver);
}
