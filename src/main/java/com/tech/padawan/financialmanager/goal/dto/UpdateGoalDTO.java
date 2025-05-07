package com.tech.padawan.financialmanager.goal.dto;

import java.util.Date;

public record UpdateGoalDTO(
        String name,
        double targetAmount,
        double savedAmount,
        String reason,
        Date deadline
) {
}
