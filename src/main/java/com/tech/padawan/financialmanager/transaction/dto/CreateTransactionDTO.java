package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;

import java.util.Date;

public record CreateTransactionDTO(
        double value,
        String description,
        TransactionType type,
        Long userId
) {
}
