package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record CreateTransactionDTO(
        @NotNull(message = "Value is required")
        @Min(value = 0, message = "Value most be a positive number")
        double value,
        String description,
        @NotNull(message = "Category is required")
        TransactionCategory category,
        @NotNull(message = "UserId is required")
        @Min(value = 0, message = "UserId cannot be a negative number")
        Long userId
) {
}
