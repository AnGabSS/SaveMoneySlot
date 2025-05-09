package com.tech.padawan.financialmanager.goal.repository;

import com.tech.padawan.financialmanager.goal.model.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    Page<Goal> findAllByUserId(Pageable pageable, Long userId);
}
