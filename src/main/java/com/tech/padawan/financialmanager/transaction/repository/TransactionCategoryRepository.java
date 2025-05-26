package com.tech.padawan.financialmanager.transaction.repository;

import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
    Page<TransactionCategory> findAllByUserId(Pageable pageable, Long userId);
}
