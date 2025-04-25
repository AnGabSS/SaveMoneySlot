package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.model.User;

import java.util.Date;

public record SearchedTransactionDTO(
        Long id,
        double value,
        String description,
        TransactionType type,
        Date createdAt,
        Long userId
) {
    public static SearchedTransactionDTO from(Transaction transaction) {
        return new SearchedTransactionDTO(
                transaction.getId(),
                transaction.getValue(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getCreatedAt(),
                transaction.getUser().getId()
        );
    }
}
