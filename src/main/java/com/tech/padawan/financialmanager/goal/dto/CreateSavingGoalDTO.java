package com.tech.padawan.financialmanager.goal.dto;

import com.tech.padawan.financialmanager.goal.model.GoalType;
import com.tech.padawan.financialmanager.goal.model.RecurrencePeriod;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoalType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record CreateGoalDTO(
        @NotNull GoalType goalType,
        @NotBlank String name,
        String reason,
        @NotNull Long partyId,

        BigDecimal targetAmount,
        BigDecimal savedAmount,
        LocalDate deadline,

        RecurrencePeriod recurrencePeriod,
        SpendingLimitGoalType limitType,
        BigDecimal limitAmount,
        BigDecimal limitPercentage
) {}