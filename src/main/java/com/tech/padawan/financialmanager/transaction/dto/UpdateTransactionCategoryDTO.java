package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateTransactionCategoryDTO(
        @NotNull(message = "Name is required")
        String name,
        @NotNull(message = "Type is required")
        TransactionType type
) {
}
