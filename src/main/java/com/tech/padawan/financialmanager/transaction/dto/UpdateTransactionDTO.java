package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;

public record UpdateTransactionDTO(
        double value,
        String description,
        TransactionType type) {
}
