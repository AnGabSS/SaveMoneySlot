package com.tech.padawan.financialmanager.goal.dto;

import com.tech.padawan.financialmanager.goal.model.GoalType;
import com.tech.padawan.financialmanager.goal.model.RecurrencePeriod;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSavingGoalDTO(
        @NotBlank String name,
        String reason,
        @NotNull Long partyId,
        @NotNull @DecimalMin("0.0") BigDecimal targetAmount,
        BigDecimal savedAmount,
        LocalDate deadline
) {}