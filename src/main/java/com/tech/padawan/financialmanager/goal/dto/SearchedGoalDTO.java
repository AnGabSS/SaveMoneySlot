package com.tech.padawan.financialmanager.goal.dto;

import com.tech.padawan.financialmanager.goal.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record SearchedGoalDTO(
        // Campos Comuns
        Long id,
        String name,
        String reason,
        boolean isCompleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long partyId,
        GoalType goalType,
        BigDecimal targetAmount,
        BigDecimal savedAmount,
        LocalDate deadline,
        RecurrencePeriod recurrencePeriod,
        SpendingLimitGoalType limitType,
        BigDecimal limitAmount,
        BigDecimal limitPercentage
) {

    public static SearchedGoalDTO from(Goal goal) {
        if (goal == null) {
            return null;
        }

        Long id = goal.getId();
        String name = goal.getName();
        String reason = goal.getReason();
        boolean isCompleted = goal.isCompleted();
        LocalDateTime createdAt = goal.getCreatedAt();
        LocalDateTime updatedAt = goal.getUpdatedAt();
        Long partyId = (goal.getParty() != null) ? goal.getParty().getId() : null;

        GoalType goalType = null;
        BigDecimal targetAmount = null;
        BigDecimal savedAmount = null;
        LocalDate deadline = null;
        RecurrencePeriod recurrencePeriod = null;
        SpendingLimitGoalType limitType = null;
        BigDecimal limitAmount = null;
        BigDecimal limitPercentage = null;

        if (goal instanceof SavingGoal savingGoal) {
            goalType = GoalType.SAVING;
            targetAmount = savingGoal.getTargetAmount();
            savedAmount = savingGoal.getSavedAmount();
            deadline = savingGoal.getDeadline();
        } else if (goal instanceof SpendingLimitGoal spendingLimitGoal) {
            goalType = GoalType.SPENDING_LIMIT;
            recurrencePeriod = spendingLimitGoal.getRecurrencePeriod();
            limitType = spendingLimitGoal.getLimitType();
            limitAmount = spendingLimitGoal.getLimitAmount();
            limitPercentage = spendingLimitGoal.getLimitPercentage();
        }

        return new SearchedGoalDTO(
                id, name, reason, isCompleted, createdAt, updatedAt, partyId,
                goalType,
                targetAmount, savedAmount, deadline,
                recurrencePeriod, limitType, limitAmount, limitPercentage
        );
    }
}