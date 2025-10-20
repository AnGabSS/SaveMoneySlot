package com.tech.padawan.financialmanager.goal.dto;

import com.tech.padawan.financialmanager.goal.model.RecurrencePeriod;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateSpendingLimitGoalDTO(
        @NotBlank String name,
        String reason,
        @NotNull Long partyId,
        @NotNull RecurrencePeriod recurrencePeriod,
        @NotNull SpendingLimitGoalType limitType,
        @DecimalMin("0.0") BigDecimal limitAmount,
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal limitPercentage
) {}