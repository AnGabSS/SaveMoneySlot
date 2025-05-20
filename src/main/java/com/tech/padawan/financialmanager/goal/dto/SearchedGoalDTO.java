package com.tech.padawan.financialmanager.goal.dto;

import com.tech.padawan.financialmanager.goal.model.Goal;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        Date updatedAt,
        String user
) {

    public static SearchedGoalDTO from(Goal goal) {
        return from(goal, true);
    }

    public static SearchedGoalDTO from(Goal goal, boolean includeUserUrl) {
        Long userId = goal.getUser() != null ? goal.getUser().getId() : null;

        String userUrl = null;
        if (includeUserUrl && userId != null) {
            try {
                userUrl = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/user/{id}")
                        .buildAndExpand(userId)
                        .toUriString();
            } catch (IllegalStateException e) {
                userUrl = null;
            }
        }

        return new SearchedGoalDTO(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getSavedAmount(),
                goal.getReason(),
                goal.getDeadline(),
                goal.isCompleted(),
                goal.getCreatedAt(),
                goal.getUpdatedAt(),
                userUrl
        );
    }
}
