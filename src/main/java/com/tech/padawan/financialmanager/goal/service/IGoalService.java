package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.*;
import com.tech.padawan.financialmanager.goal.model.SavingGoal;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoal;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;


public interface IGoalService {


    SavingGoal createSavingGoal(CreateSavingGoalDTO dto);
    SpendingLimitGoal createSpendingLimitGoal(CreateSpendingLimitGoalDTO dto);
    SearchedGoalDTO updateSavingGoal(Long id, UpdateSavingGoalDTO dto);
    SearchedGoalDTO updateSpendingLimitGoal(Long id, UpdateSpendingLimitGoalDTO dto);
    SearchedGoalDTO updateSaveAmount(Long id, BigDecimal newSaveAmount);
    SearchedGoalDTO getById(Long id);
    Page<SearchedGoalDTO> findAllByPartyId(Long partyId, int page, int size, String orderBy, String direction);
    String delete(Long id);
}