package com.tech.padawan.financialmanager.goal.dto;

import com.tech.padawan.financialmanager.goal.model.Goal;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.model.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.ColumnDefault;
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
        Long userId = goal.getUser() != null ? goal.getUser().getId() : null;

        String userUrl = userId != null
                ? ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/user/{id}")
                .buildAndExpand(userId)
                .toUriString()
                : null;

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
