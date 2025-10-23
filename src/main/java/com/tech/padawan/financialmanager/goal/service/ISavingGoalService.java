package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.*;
import com.tech.padawan.financialmanager.goal.model.SavingGoal;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoal;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ISavingGoalService {
    SavingGoal createSavingGoal(CreateSavingGoalDTO dto);
    SearchedGoalDTO updateSavingGoal(Long id, UpdateSavingGoalDTO dto);
    SearchedGoalDTO updateSaveAmount(Long id, BigDecimal newSaveAmount);
    String finishSavingGoal(Long id);
}
