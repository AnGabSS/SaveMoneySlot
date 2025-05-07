package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.model.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IGoalService {
    Page<SearchedGoalDTO> findAll(Pageable pageable);
    SearchedGoalDTO findById(Long id);
    Goal save(Goal goal);
    Page<SearchedGoalDTO> findAllByUserId(Pageable pageable, Long userId);
    void deleteById(Long id);
}
