package com.tech.padawan.financialmanager.goal.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

public record SearchedGoalDTO(
        Long id,
        String name,
        double targetAmount,
        double savedAmount,
        String reason,
        Date deadline,
        boolean isCompleted,
        Date createdAt,
        Date updatedAt
) {
}
