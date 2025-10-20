package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.*;
import com.tech.padawan.financialmanager.goal.model.*;
import com.tech.padawan.financialmanager.goal.repository.GoalRepository;
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GoalServiceTest {

    @Mock
    private GoalRepository repository;

    @Mock
    private IPartyService partyService;

    @InjectMocks
    private GoalService service;

    private Party mockParty;
    private SavingGoal mockSavingGoal;
    private SpendingLimitGoal mockSpendingLimitGoal;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockParty = Party.builder()
                .id(1L)
                .build();

        mockSavingGoal = new SavingGoal();
        mockSavingGoal.setId(1L);
        mockSavingGoal.setName("Buy a Car");
        mockSavingGoal.setParty(mockParty);
        mockSavingGoal.setTargetAmount(BigDecimal.valueOf(50000));
        mockSavingGoal.setSavedAmount(BigDecimal.valueOf(5000));

        mockSpendingLimitGoal = new SpendingLimitGoal();
        mockSpendingLimitGoal.setId(2L);
        mockSpendingLimitGoal.setName("Monthly Food Budget");
        mockSpendingLimitGoal.setParty(mockParty);
        mockSpendingLimitGoal.setLimitAmount(BigDecimal.valueOf(800));
    }


    @Test
    @DisplayName("Should create a Saving Goal successfully")
    void shouldCreateSavingGoal() {
        CreateSavingGoalDTO dto = new CreateSavingGoalDTO("New Car", "For family trips", 1L, new BigDecimal("60000"), BigDecimal.ZERO, LocalDate.now().plusYears(2));
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(repository.save(any(SavingGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SavingGoal result = service.createSavingGoal(dto);

        assertNotNull(result);
        assertEquals(dto.name(), result.getName());
        assertEquals(dto.targetAmount(), result.getTargetAmount());
        verify(partyService).getById(1L);
        verify(repository).save(any(SavingGoal.class));
    }

    @Test
    @DisplayName("Should create a Spending Limit Goal successfully")
    void shouldCreateSpendingLimitGoal() {
        CreateSpendingLimitGoalDTO dto = new CreateSpendingLimitGoalDTO("Groceries", null, 1L, RecurrencePeriod.MONTHLY, SpendingLimitGoalType.AMOUNT, new BigDecimal("1200"), null);
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(repository.save(any(SpendingLimitGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SpendingLimitGoal result = service.createSpendingLimitGoal(dto);

        assertNotNull(result);
        assertEquals(dto.name(), result.getName());
        assertEquals(dto.limitAmount(), result.getLimitAmount());
        verify(partyService).getById(1L);
        verify(repository).save(any(SpendingLimitGoal.class));
    }


    @Test
    @DisplayName("Should update a Saving Goal successfully")
    void shouldUpdateSavingGoal() {
        UpdateSavingGoalDTO dto = new UpdateSavingGoalDTO("Updated Car Name", "New reason", new BigDecimal("55000"), null);
        when(repository.findById(1L)).thenReturn(Optional.of(mockSavingGoal));
        when(repository.save(any(SavingGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateSavingGoal(1L, dto);

        ArgumentCaptor<SavingGoal> captor = ArgumentCaptor.forClass(SavingGoal.class);
        verify(repository).save(captor.capture());

        SavingGoal updatedGoal = captor.getValue();
        assertEquals("Updated Car Name", updatedGoal.getName());
        assertEquals(new BigDecimal("55000"), updatedGoal.getTargetAmount());
    }

    @Test
    @DisplayName("Should throw exception when updating a non-saving goal as a saving goal")
    void shouldThrowWhenUpdatingWrongType() {
        UpdateSavingGoalDTO dto = new UpdateSavingGoalDTO("Name", null, null, null);
        when(repository.findById(2L)).thenReturn(Optional.of(mockSpendingLimitGoal)); // Retorna o tipo errado

        assertThrows(GoalNotFoundException.class, () -> service.updateSavingGoal(2L, dto));
    }

    @Test
    @DisplayName("Should update only saved amount for a Saving Goal")
    void shouldUpdateSaveAmount() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockSavingGoal));
        when(repository.save(any(SavingGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateSaveAmount(1L, new BigDecimal("10000"));

        ArgumentCaptor<SavingGoal> captor = ArgumentCaptor.forClass(SavingGoal.class);
        verify(repository).save(captor.capture());

        assertEquals(new BigDecimal("10000"), captor.getValue().getSavedAmount());
    }

    @Test
    @DisplayName("Should throw exception when updating saved amount on a non-saving goal")
    void shouldThrowWhenUpdateSaveAmountOnWrongType() {
        when(repository.findById(2L)).thenReturn(Optional.of(mockSpendingLimitGoal));

        assertThrows(GoalNotFoundException.class, () -> service.updateSaveAmount(2L, new BigDecimal("100")));
        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("Should get goal by ID")
    void shouldGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockSavingGoal));
        SearchedGoalDTO result = service.getById(1L);
        assertNotNull(result);
        assertEquals(mockSavingGoal.getName(), result.name());
    }

    @Test
    @DisplayName("Should throw GoalNotFoundException when getting non-existent goal")
    void shouldThrowWhenGetByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(GoalNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    @DisplayName("Should find all goals by Party ID paginated")
    void shouldFindAllByPartyId() {
        Page<Goal> page = new PageImpl<>(List.of(mockSavingGoal));
        when(repository.findAllByPartyId(any(Pageable.class), eq(1L))).thenReturn(page);

        Page<SearchedGoalDTO> result = service.findAllByPartyId(1L, 0, 10, "id", "ASC");

        assertEquals(1, result.getTotalElements());
        verify(repository).findAllByPartyId(any(Pageable.class), eq(1L));
    }

    @Test
    @DisplayName("Should delete a goal by ID")
    void shouldDeleteGoal() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        String result = service.delete(1L);

        assertEquals("Goal deleted successfully.", result);
        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }
}