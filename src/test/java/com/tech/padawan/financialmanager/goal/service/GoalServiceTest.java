package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.CreateSpendingLimitGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateSpendingLimitGoalDTO;
import com.tech.padawan.financialmanager.goal.model.*;
import com.tech.padawan.financialmanager.goal.repository.GoalRepository;
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.service.ITransactionCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository repository;
    @Mock
    private IPartyService partyService;
    @Mock
    private ITransactionCategoryService transactionCategoryService;

    @InjectMocks
    private GoalService service;

    private Party mockParty;
    private Goal mockGoal;
    private SpendingLimitGoal mockSpendingLimitGoal;
    private TransactionCategory mockCategory;

    @BeforeEach
    void setup() {
        mockParty = Party.builder().id(1L).build();

        mockGoal = new SavingGoal();
        mockGoal.setId(1L);
        mockGoal.setName("Buy a Car");
        mockGoal.setParty(mockParty);
        mockGoal.setCompleted(false);

        mockSpendingLimitGoal = new SpendingLimitGoal();
        mockSpendingLimitGoal.setId(2L);
        mockSpendingLimitGoal.setName("Old Name");
        mockSpendingLimitGoal.setReason("Old Reason");
        mockSpendingLimitGoal.setInitialDate(LocalDate.of(2025, 1, 1));
        mockSpendingLimitGoal.setFinalDate(LocalDate.of(2025, 1, 31));
        mockSpendingLimitGoal.setLimitType(SpendingLimitGoalType.AMOUNT);
        mockSpendingLimitGoal.setLimitAmount(new BigDecimal("1000.00"));
        mockSpendingLimitGoal.setLimitPercentage(null);
        mockSpendingLimitGoal.setParty(mockParty);

        mockCategory = new TransactionCategory();
        mockCategory.setId(1L);
    }

    @Test
    @DisplayName("Should create a Spending Limit Goal successfully")
    void shouldCreateSpendingLimitGoal() {
        CreateSpendingLimitGoalDTO dto = new CreateSpendingLimitGoalDTO("Groceries", null, 1L, SpendingLimitGoalType.AMOUNT, new BigDecimal("1200"), null, LocalDate.now(), LocalDate.now().plusMonths(1), 1L);
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(transactionCategoryService.getEntityById(1L)).thenReturn(mockCategory);
        when(repository.save(any(SpendingLimitGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SpendingLimitGoal result = service.createSpendingLimitGoal(dto);

        assertNotNull(result);
        assertEquals(dto.name(), result.getName());
        verify(repository).save(any(SpendingLimitGoal.class));
    }

    @Test
    @DisplayName("Should throw exception when creating an AMOUNT goal with a null amount")
    void shouldThrowWhenCreateAmountGoalWithNullAmount() {
        CreateSpendingLimitGoalDTO dto = new CreateSpendingLimitGoalDTO("Groceries", null, 1L, SpendingLimitGoalType.AMOUNT, null, null, LocalDate.now(), LocalDate.now().plusMonths(1), 1L);
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(transactionCategoryService.getEntityById(1L)).thenReturn(mockCategory);

        assertThrows(IllegalArgumentException.class, () -> service.createSpendingLimitGoal(dto));
    }

    @Test
    @DisplayName("Should throw exception when creating a PERCENTUAL goal with a null percentage")
    void shouldThrowWhenCreatePercentualGoalWithNullPercentage() {
        CreateSpendingLimitGoalDTO dto = new CreateSpendingLimitGoalDTO("Groceries", null, 1L, SpendingLimitGoalType.PERCENTUAL, null, null, LocalDate.now(), LocalDate.now().plusMonths(1), 1L);
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(transactionCategoryService.getEntityById(1L)).thenReturn(mockCategory);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.createSpendingLimitGoal(dto));
        assertEquals("Limit percentage is required for type PERCENTUAL.", exception.getMessage());
    }

    @Test
    @DisplayName("Should get a goal by ID")
    void shouldGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockGoal));

        SearchedGoalDTO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(mockGoal.getName(), result.name());
    }

    @Test
    @DisplayName("Should throw GoalNotFoundException when getting a non-existent goal")
    void shouldThrowWhenGetByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    @DisplayName("Should find all goals for a Party in a paginated way")
    void shouldFindAllByPartyId() {
        Page<Goal> page = new PageImpl<>(List.of(mockGoal));
        when(repository.findAllByPartyId(any(Pageable.class), eq(1L))).thenReturn(page);

        Page<SearchedGoalDTO> result = service.findAllByPartyId(1L, 0, 10, "id", "ASC");

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should update only non-null fields of a Spending Limit Goal")
    void shouldUpdateOnlyNonNullFieldsOfSpendingLimitGoal() {
        LocalDate newFinalDate = LocalDate.of(2025, 2, 28);
        UpdateSpendingLimitGoalDTO dto = new UpdateSpendingLimitGoalDTO(
                "New Name",
                null,
                null,
                new BigDecimal("1500.00"),
                null,
                null,
                newFinalDate
        );
        when(repository.findById(2L)).thenReturn(Optional.of(mockSpendingLimitGoal));
        when(repository.save(any(SpendingLimitGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateSpendingLimitGoal(2L, dto);

        assertEquals("New Name", mockSpendingLimitGoal.getName());
        assertEquals(0, new BigDecimal("1500.00").compareTo(mockSpendingLimitGoal.getLimitAmount()));
        assertEquals(newFinalDate, mockSpendingLimitGoal.getFinalDate());
        assertEquals("Old Reason", mockSpendingLimitGoal.getReason());
        assertEquals(LocalDate.of(2025, 1, 1), mockSpendingLimitGoal.getInitialDate());
        assertEquals(SpendingLimitGoalType.AMOUNT, mockSpendingLimitGoal.getLimitType());

        verify(repository).findById(2L);
        verify(repository).save(mockSpendingLimitGoal);
    }

    @Test
    @DisplayName("Should throw exception when trying to update a goal that is not a Spending Limit Goal")
    void shouldThrowWhenUpdateGoalOfWrongType() {
        UpdateSpendingLimitGoalDTO dto = new UpdateSpendingLimitGoalDTO("New Name", null, null, null, null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(mockGoal));

        assertThrows(GoalNotFoundException.class, () -> service.updateSpendingLimitGoal(1L, dto));
    }

    @Test
    @DisplayName("Should mark a goal as complete")
    void shouldCompleteGoal() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockGoal));
        assertFalse(mockGoal.isCompleted());

        String result = service.complete(1L);

        assertEquals("Goal successfully completed", result);
        assertTrue(mockGoal.isCompleted());
        verify(repository).save(mockGoal);
    }

    @Test
    @DisplayName("Should throw exception when trying to complete a non-existent goal")
    void shouldThrowWhenCompleteNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> service.complete(99L));
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

    @Test
    @DisplayName("Should throw exception when trying to delete a non-existent goal")
    void shouldThrowWhenDeleteNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(GoalNotFoundException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(anyLong());
    }
}