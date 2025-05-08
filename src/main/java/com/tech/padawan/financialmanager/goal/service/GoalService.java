package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.CreateGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateGoalDTO;
import com.tech.padawan.financialmanager.goal.model.Goal;
import com.tech.padawan.financialmanager.goal.repository.GoalRepository;
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GoalService implements IGoalService {

    @Autowired
    private GoalRepository repository;

    @Autowired
    private IUserService userService;

    @Override
    public Page<SearchedGoalDTO> findAll(int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<Goal> list = repository.findAll(pageRequest);
        return list.map(SearchedGoalDTO::from);
    }

    @Override
    public SearchedGoalDTO getById(Long id) {
        Goal goal = Optional.of(repository.findById(id).orElseThrow(() -> new GoalNotFoundException("Goal with id " + id + " not found."))).get();
        return SearchedGoalDTO.from(goal);
    }

    @Override
    public Goal create(CreateGoalDTO goalDTO) {
        User userFind = userService.getUserEntityById(goalDTO.userId());
        Goal goal = Goal.builder()
                .name(goalDTO.name())
                .targetAmount(goalDTO.targetAmount())
                .savedAmount(goalDTO.savedAmount())
                .reason(goalDTO.reason())
                .deadline(goalDTO.deadline())
                .user(userFind)
                .build();

        return repository.save(goal);
    }

    @Override
    public SearchedGoalDTO update(Long id, UpdateGoalDTO goal) {
        Goal oldGoal = Optional.of(repository.findById(id).orElseThrow(() -> new GoalNotFoundException("Goal with id " + id + " not found."))).get();;
        oldGoal.setName(goal.name());
        oldGoal.setTargetAmount(goal.targetAmount());
        oldGoal.setSavedAmount(goal.savedAmount());
        oldGoal.setReason(goal.reason());
        oldGoal.setDeadline(goal.deadline());
        Goal newGoal = repository.save(oldGoal);
        return SearchedGoalDTO.from(newGoal);
    }

    @Override
    public Page<SearchedGoalDTO> findAllByUserId(Long userId, int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<Goal> list = repository.findAllByUserId(pageRequest, userId);
        return list.map(SearchedGoalDTO::from);
    }

    @Override
    public String delete(Long id) {
        this.getById(id);
        repository.deleteById(id);
        return "Goal deleted";
    }

    @Override
    public SearchedGoalDTO updateSaveAmount(Long id, double newSaveAmount) {
        Goal oldGoal = Optional.of(repository.findById(id).orElseThrow(() -> new GoalNotFoundException("Goal with id " + id + " not found."))).get();;
        oldGoal.setSavedAmount(newSaveAmount);
        Goal newGoal = repository.save(oldGoal);
        return SearchedGoalDTO.from(newGoal);
    }
}
