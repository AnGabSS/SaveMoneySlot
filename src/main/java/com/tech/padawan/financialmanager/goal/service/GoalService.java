package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.*;
import com.tech.padawan.financialmanager.goal.model.*;
import com.tech.padawan.financialmanager.goal.repository.GoalRepository;
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class GoalService implements IGoalService {

    private final GoalRepository repository;
    private final IPartyService partyService;

    public GoalService(GoalRepository repository, IPartyService partyService) {
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
        goal.setDeadline(dto.deadline());
        goal.setSavedAmount(dto.savedAmount() != null ? dto.savedAmount() : BigDecimal.ZERO);

        return repository.save(goal);
    }

    @Override
    @Transactional
    public SpendingLimitGoal createSpendingLimitGoal(CreateSpendingLimitGoalDTO dto) {
        Party party = partyService.getById(dto.partyId());
        validateSpendingLimit(dto);

        SpendingLimitGoal goal = new SpendingLimitGoal();
        goal.setName(dto.name());
        goal.setReason(dto.reason());
        goal.setParty(party);
        goal.setRecurrencePeriod(dto.recurrencePeriod());
        goal.setLimitType(dto.limitType());
        goal.setLimitAmount(dto.limitAmount());
        goal.setLimitPercentage(dto.limitPercentage());

        return repository.save(goal);
    }

    @Override
    @Transactional
    public SearchedGoalDTO updateSavingGoal(Long id, UpdateSavingGoalDTO dto) {
        SavingGoal goal = findSavingGoalById(id);

        if (dto.name() != null) goal.setName(dto.name());
        if (dto.reason() != null) goal.setReason(dto.reason());
        if (dto.targetAmount() != null) goal.setTargetAmount(dto.targetAmount());
        if (dto.deadline() != null) goal.setDeadline(dto.deadline());

        return SearchedGoalDTO.from(repository.save(goal));
    }

    @Override
    @Transactional
    public SearchedGoalDTO updateSpendingLimitGoal(Long id, UpdateSpendingLimitGoalDTO dto) {
        SpendingLimitGoal goal = findSpendingLimitGoalById(id);

        if (dto.name() != null) goal.setName(dto.name());
        if (dto.reason() != null) goal.setReason(dto.reason());
        if (dto.recurrencePeriod() != null) goal.setRecurrencePeriod(dto.recurrencePeriod());
        if (dto.limitType() != null) goal.setLimitType(dto.limitType());
        if (dto.limitAmount() != null) goal.setLimitAmount(dto.limitAmount());
        if (dto.limitPercentage() != null) goal.setLimitPercentage(dto.limitPercentage());

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
    @Transactional(readOnly = true)
    public SearchedGoalDTO getById(Long id) {
        Goal goal = repository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Goal with id " + id + " not found."));
        return SearchedGoalDTO.from(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SearchedGoalDTO> findAllByPartyId(Long partyId, int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<Goal> list = repository.findAllByPartyId(pageRequest, partyId);
        return list.map(SearchedGoalDTO::from);
    }

    @Override
    @Transactional
    public String delete(Long id) {
        if (!repository.existsById(id)) {
            throw new GoalNotFoundException("Goal with id " + id + " not found.");
        }
        repository.deleteById(id);
        return "Goal deleted successfully.";
    }


    // --- MÉTODOS AUXILIARES ---

    private SavingGoal findSavingGoalById(Long id) {
        return repository.findById(id)
                .filter(g -> g instanceof SavingGoal)
                .map(g -> (SavingGoal) g)
                .orElseThrow(() -> new GoalNotFoundException("Saving Goal with id " + id + " not found."));
    }

    private SpendingLimitGoal findSpendingLimitGoalById(Long id) {
        return repository.findById(id)
                .filter(g -> g instanceof SpendingLimitGoal)
                .map(g -> (SpendingLimitGoal) g)
                .orElseThrow(() -> new GoalNotFoundException("Spending Limit Goal with id " + id + " not found."));
    }

    private void validateSpendingLimit(CreateSpendingLimitGoalDTO dto) {
        if (dto.limitType() == SpendingLimitGoalType.AMOUNT && dto.limitAmount() == null) {
            throw new IllegalArgumentException("Limit amount is required for type AMOUNT.");
        }
        if (dto.limitType() == SpendingLimitGoalType.PERCENTUAL && dto.limitPercentage() == null) {
            throw new IllegalArgumentException("Limit percentage is required for type PERCENTUAL.");
        }
    }
}