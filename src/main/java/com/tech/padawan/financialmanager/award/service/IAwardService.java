package com.tech.padawan.financialmanager.award.service;

import com.tech.padawan.financialmanager.award.dto.AwardResultDTO;

public interface IAwardService {
    AwardResultDTO receiveSavingGoalAward(Long id);
    AwardResultDTO finishSpendingLimitGoal(Long id);
}
