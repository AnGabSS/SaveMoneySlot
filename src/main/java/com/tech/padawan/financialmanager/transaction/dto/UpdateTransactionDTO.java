package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateTransactionDTO(
        @NotNull(message = "Value is required")
        @Min(value = 0, message = "Value most be a positive number")
        double value,
        String description,
        @NotNull(message = "Type is required")
        TransactionType type
) {
}
