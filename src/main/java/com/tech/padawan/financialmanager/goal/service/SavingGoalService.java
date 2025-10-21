package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.CreateSavingGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateSavingGoalDTO;
import com.tech.padawan.financialmanager.goal.model.SavingGoal;
import com.tech.padawan.financialmanager.goal.repository.SavingGoalRepository;
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SavingGoalService implements ISavingGoalService{

    private final SavingGoalRepository repository;
    private final IPartyService partyService;

    public SavingGoalService(SavingGoalRepository repository, IPartyService partyService){
        this.repository = repository;
        this.partyService = partyService;
    }

    @Override
    @Transactional
    public SavingGoal createSavingGoal(CreateSavingGoalDTO dto) {
        Party party = partyService.getById(dto.partyId());

        SavingGoal goal = new SavingGoal();
        goal.setName(dto.name());
        goal.setReason(dto.reason());
        goal.setParty(party);
        goal.setTargetAmount(dto.targetAmount());
        goal.setCreatedAt(LocalDateTime.now());
        goal.setSavedAmount(dto.savedAmount() != null ? dto.savedAmount() : BigDecimal.ZERO);

        return repository.save(goal);
    }


    @Override
    @Transactional
    public SearchedGoalDTO updateSavingGoal(Long id, UpdateSavingGoalDTO dto) {
        SavingGoal goal = findSavingGoalById(id);

        if (dto.name() != null) goal.setName(dto.name());
        if (dto.reason() != null) goal.setReason(dto.reason());
        if (dto.targetAmount() != null) goal.setTargetAmount(dto.targetAmount());

        return SearchedGoalDTO.from(repository.save(goal));
    }


    @Override
    @Transactional
    public SearchedGoalDTO updateSaveAmount(Long id, BigDecimal newSaveAmount) {
        SavingGoal goal = findSavingGoalById(id);
        goal.setSavedAmount(newSaveAmount);
        return SearchedGoalDTO.from(repository.save(goal));
    }


    @Override
    public String finishSavingGoal(Long id) {
        SavingGoal goal = repository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Goal with id " + id + " not found."));
        goal.setCompleted(true);
        repository.save(goal);
        return "Goal successfully finished";
    }


    private SavingGoal findSavingGoalById(Long id) {
        return repository.findById(id)
                .filter(g -> g instanceof SavingGoal)
                .map(g -> (SavingGoal) g)
                .orElseThrow(() -> new GoalNotFoundException("Saving Goal with id " + id + " not found."));
    }
}
