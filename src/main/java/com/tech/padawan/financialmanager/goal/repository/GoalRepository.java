package com.tech.padawan.financialmanager.goal.repository;

import com.tech.padawan.financialmanager.goal.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
