package com.tech.padawan.financialmanager.transaction.repository;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByPartyIdAndDescriptionContainingIgnoreCase(Pageable pageable, Long partyId, String description);
    List<Transaction> findAllByPartyIdAndCreatedAtBetween(
            Long partyId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
    List<Transaction> findAllByPartyIdAndCategoryType(
            Long partyId,
            TransactionType type
    );
    List<Transaction> findAllByPartyIdAndCreatedAtBetweenAndCategoryType(
            Long partyId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            TransactionType type
    );
    List<Transaction> findAllByPartyIdAndCreatedAtBetweenAndCategory(
            Long partyId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            TransactionCategory category
    );
}
