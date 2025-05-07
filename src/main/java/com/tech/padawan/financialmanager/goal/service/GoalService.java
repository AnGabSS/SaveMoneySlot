package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.model.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GoalService implements IGoalService {
    @Override
    public Page<SearchedGoalDTO> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public SearchedGoalDTO findById(Long id) {
        return null;
    }

    @Override
    public Goal save(Goal goal) {
        return null;
    }

    @Override
    public Page<SearchedGoalDTO> findAllByUserId(Pageable pageable, Long userId) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
