package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.CreateGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateGoalDTO;
import com.tech.padawan.financialmanager.goal.model.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IGoalService {
    Page<SearchedGoalDTO> findAll(int page, int size, String orderBy, String direction);
    SearchedGoalDTO getById(Long id);
    Goal create(CreateGoalDTO goal);
    SearchedGoalDTO update(Long id, UpdateGoalDTO goal);
    Page<SearchedGoalDTO> findAllByUserId(Long userId, int page, int size, String orderBy, String direction);
    String delete(Long id);
}
