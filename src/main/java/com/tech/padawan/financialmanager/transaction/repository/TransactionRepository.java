package com.tech.padawan.financialmanager.transaction.repository;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByUserId(Pageable pageable, Long userId);
}
