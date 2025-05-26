package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateTransactionCategoryDTO(
        @NotNull(message = "Name is required")
        String name,
        @NotNull(message = "Type is required")
        TransactionType type,
        @NotNull(message = "UserId is required")
        @Min(value = 0, message = "UserId cannot be a negative number")
        Long userId
) {
}
