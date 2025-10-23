package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.CreateSavingGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateSavingGoalDTO;
import com.tech.padawan.financialmanager.goal.model.SavingGoal;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoal;
import com.tech.padawan.financialmanager.goal.repository.SavingGoalRepository; // Repositório correto
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingGoalServiceTest {

    @Mock
    private SavingGoalRepository repository;
    @Mock
    private IPartyService partyService;

    @InjectMocks
    private SavingGoalService goalService;

    private Party mockParty;
    private SavingGoal mockSavingGoal;
    private SpendingLimitGoal mockSpendingLimitGoal;

    @BeforeEach
    void setup() {
        mockParty = Party.builder().id(1L).build();

        mockSavingGoal = new SavingGoal();
        mockSavingGoal.setId(1L);
        mockSavingGoal.setName("Buy a Car");
        mockSavingGoal.setParty(mockParty);
        mockSavingGoal.setTargetAmount(new BigDecimal("50000"));
        mockSavingGoal.setSavedAmount(new BigDecimal("5000"));

        mockSpendingLimitGoal = new SpendingLimitGoal();
        mockSpendingLimitGoal.setId(2L);
        mockSpendingLimitGoal.setParty(mockParty);
    }

    @Test
    @DisplayName("Should create a Saving Goal successfully")
    void shouldCreateSavingGoal() {
        CreateSavingGoalDTO dto = new CreateSavingGoalDTO("New Car", "For family trips", 1L, new BigDecimal("60000"), BigDecimal.ZERO);
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(repository.save(any(SavingGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SavingGoal result = goalService.createSavingGoal(dto);

        assertNotNull(result);
        assertEquals(dto.name(), result.getName());
        assertEquals(dto.targetAmount(), result.getTargetAmount());
        verify(partyService).getById(1L);
        verify(repository).save(any(SavingGoal.class));
    }

    @Test
    @DisplayName("Should update a Saving Goal successfully")
    void shouldUpdateSavingGoal() {
        UpdateSavingGoalDTO dto = new UpdateSavingGoalDTO("Updated Car Name", "New reason", new BigDecimal("55000"));
        when(repository.findById(1L)).thenReturn(Optional.of(mockSavingGoal));
        when(repository.save(any(SavingGoal.class))).thenReturn(mockSavingGoal); // Mock para save

        goalService.updateSavingGoal(1L, dto);

        ArgumentCaptor<SavingGoal> captor = ArgumentCaptor.forClass(SavingGoal.class);
        verify(repository).save(captor.capture());

        SavingGoal updatedGoal = captor.getValue();
        assertEquals("Updated Car Name", updatedGoal.getName());
        assertEquals(new BigDecimal("55000"), updatedGoal.getTargetAmount());
    }

    @Test
    @DisplayName("Should throw exception when updating a non-saving goal as a saving goal")
    void shouldThrowWhenUpdatingWrongType() {
        UpdateSavingGoalDTO dto = new UpdateSavingGoalDTO("Name", null, null);
        when(repository.findById(2L)).thenReturn(Optional.empty()); // findById de SavingGoal não achará um SpendingLimitGoal

        assertThrows(GoalNotFoundException.class, () -> goalService.updateSavingGoal(2L, dto));
    }

    @Test
    @DisplayName("Should add to saved amount for a Saving Goal")
    void shouldAddSaveAmount() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockSavingGoal));
        when(repository.save(any(SavingGoal.class))).thenReturn(mockSavingGoal);

        // O valor salvo inicial é 5000. Adicionando 10000, o novo total deve ser 15000.
        BigDecimal amountToAdd = new BigDecimal("10000");
        goalService.updateSaveAmount(1L, amountToAdd); // Assumindo que updateSaveAmount agora tem um DTO

        ArgumentCaptor<SavingGoal> captor = ArgumentCaptor.forClass(SavingGoal.class);
        verify(repository).save(captor.capture());

        assertEquals(new BigDecimal("15000"), captor.getValue().getSavedAmount());
    }
}