package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.CreateSpendingLimitGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
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

@ExtendWith(MockitoExtension.class) // Forma moderna de inicializar mocks
class GoalServiceTest {

    @Mock
    private GoalRepository repository;
    @Mock
    private IPartyService partyService;
    @Mock
    private ITransactionCategoryService transactionCategoryService; // Mock adicionado

    @InjectMocks
    private GoalService service;

    private Party mockParty;
    private SavingGoal mockSavingGoal;
    private TransactionCategory mockCategory;

    @BeforeEach
    void setup() {
        mockParty = Party.builder().id(1L).build();
        mockSavingGoal = new SavingGoal();
        mockSavingGoal.setId(1L);
        mockSavingGoal.setName("Buy a Car");
        mockSavingGoal.setParty(mockParty);
        mockCategory = new TransactionCategory();
        mockCategory.setId(1L);
    }

    @Test
    @DisplayName("Should create a Spending Limit Goal successfully")
    void shouldCreateSpendingLimitGoal() {
        CreateSpendingLimitGoalDTO dto = new CreateSpendingLimitGoalDTO("Groceries", null, 1L, SpendingLimitGoalType.AMOUNT, new BigDecimal("1200"), null, LocalDate.now(), LocalDate.now().plusMonths(1), 1L);
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(transactionCategoryService.getEntityById(1L)).thenReturn(mockCategory); // Mock da busca de categoria
        when(repository.save(any(SpendingLimitGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SpendingLimitGoal result = service.createSpendingLimitGoal(dto);

        assertNotNull(result);
        assertEquals(dto.name(), result.getName());
        assertEquals(dto.limitAmount(), result.getLimitAmount());
        verify(partyService).getById(1L);
        verify(transactionCategoryService).getEntityById(1L);
        verify(repository).save(any(SpendingLimitGoal.class));
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