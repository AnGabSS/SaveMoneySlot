package com.tech.padawan.financialmanager.award.service;

import com.tech.padawan.financialmanager.award.dto.AwardResultDTO;
import com.tech.padawan.financialmanager.award.exceptions.CannotReceiveTheAwardException;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoalType;
import com.tech.padawan.financialmanager.goal.service.IGoalService;
import com.tech.padawan.financialmanager.goal.service.ISavingGoalService;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import com.tech.padawan.financialmanager.report.service.IReportService;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwardServiceTest {

    @Mock
    private IGoalService goalService;
    @Mock
    private ISavingGoalService savingGoalService;
    @Mock
    private IPartyService partyService;
    @Mock
    private IReportService reportService;

    @InjectMocks
    private AwardService awardService;

    private Party party;
    private final Long GOAL_ID = 1L;
    private final Long PARTY_ID = 10L;

    @BeforeEach
    void setUp() {
        party = Party.builder()
                .id(PARTY_ID)
                .name("Test Party")
                .points(100)
                .build();
    }

    @Test
    void receiveSavingGoalAward_Success() {
        SearchedGoalDTO goal = new SearchedGoalDTO(
                GOAL_ID, "Save for Vacation", null, false, null, null, PARTY_ID,
                null, new BigDecimal("1000.00"), new BigDecimal("1000.00"), null,
                null, null, null, null, null, null
        );
        when(goalService.getById(GOAL_ID)).thenReturn(goal);
        when(reportService.getTheMonthlyAverageValuesByType(PARTY_ID, TransactionType.INCOME)).thenReturn(new BigDecimal("2000.00"));
        when(partyService.getById(PARTY_ID)).thenReturn(party);

        AwardResultDTO result = awardService.receiveSavingGoalAward(GOAL_ID);

        assertNotNull(result);
        assertEquals(5, result.pointsCalculated());
        assertTrue(result.message().contains("Congratulations"));
        verify(partyService).updatePoints(PARTY_ID, 5);
        verify(goalService).complete(GOAL_ID);
    }

    @Test
    void receiveSavingGoalAward_ThrowsException_WhenGoalNotReached() {
        SearchedGoalDTO goal = new SearchedGoalDTO(
                GOAL_ID, "Save", null, false, null, null, PARTY_ID,
                null, new BigDecimal("1000.00"), new BigDecimal("900.00"), null,
                null, null, null, null, null, null
        );
        when(goalService.getById(GOAL_ID)).thenReturn(goal);

        CannotReceiveTheAwardException exception = assertThrows(
                CannotReceiveTheAwardException.class,
                () -> awardService.receiveSavingGoalAward(GOAL_ID)
        );
        assertTrue(exception.getMessage().contains("Saved amount is not enough"));
        verify(partyService, never()).updatePoints(anyLong(), anyInt());
        verify(goalService, never()).complete(anyLong());
    }

    @Test
    void receiveSavingGoalAward_ThrowsException_WhenAlreadyCompleted() {
        SearchedGoalDTO goal = new SearchedGoalDTO(
                GOAL_ID, "Save", null, true, null, null, PARTY_ID,
                null, new BigDecimal("1000.00"), new BigDecimal("1000.00"), null,
                null, null, null, null, null, null
        );
        when(goalService.getById(GOAL_ID)).thenReturn(goal);

        CannotReceiveTheAwardException exception = assertThrows(
                CannotReceiveTheAwardException.class,
                () -> awardService.receiveSavingGoalAward(GOAL_ID)
        );
        assertEquals("Award already claimed", exception.getMessage());
    }

    @Test
    void finishSpendingLimitGoal_ThrowsException_WhenPeriodNotFinished() {
        SearchedGoalDTO goal = new SearchedGoalDTO(
                GOAL_ID, "Limit Expenses", null, false, null, null, PARTY_ID,
                null, null, null, null,
                null, null, null, LocalDate.now().minusDays(10), LocalDate.now().plusDays(1), null
        );
        when(goalService.getById(GOAL_ID)).thenReturn(goal);

        CannotReceiveTheAwardException exception = assertThrows(
                CannotReceiveTheAwardException.class,
                () -> awardService.finishSpendingLimitGoal(GOAL_ID)
        );
        assertTrue(exception.getMessage().contains("days left until its end"));
    }

    @Test
    void finishSpendingLimitGoal_AmountType_Success_UnderBudget() {
        TransactionCategory foodCategory = new TransactionCategory(); // Assuming a default constructor
        SearchedGoalDTO goal = new SearchedGoalDTO(
                GOAL_ID, "Limit Food", null, false, null, null, PARTY_ID,
                null, null, null, null,
                SpendingLimitGoalType.AMOUNT, new BigDecimal("500.00"), null, LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), foodCategory
        );
        when(goalService.getById(GOAL_ID)).thenReturn(goal);
        when(reportService.getTheTotalValuesByCategoryBetweenDate(eq(PARTY_ID), any(LocalDateTime.class), any(LocalDateTime.class), eq(foodCategory))).thenReturn(new BigDecimal("400.00"));
        when(partyService.getById(PARTY_ID)).thenReturn(party);

        AwardResultDTO result = awardService.finishSpendingLimitGoal(GOAL_ID);

        assertNotNull(result);
        assertEquals(3, result.pointsCalculated());
        assertTrue(result.message().contains("Congratulations"));
        verify(partyService).updatePoints(PARTY_ID, 3);
        verify(goalService).complete(GOAL_ID);
    }

    @Test
    void finishSpendingLimitGoal_AmountType_Failure_OverBudget() {
        TransactionCategory foodCategory = new TransactionCategory();
        SearchedGoalDTO goal = new SearchedGoalDTO(
                GOAL_ID, "Limit Food", null, false, null, null, PARTY_ID,
                null, null, null, null,
                SpendingLimitGoalType.AMOUNT, new BigDecimal("500.00"), null, LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), foodCategory
        );
        when(goalService.getById(GOAL_ID)).thenReturn(goal);
        when(reportService.getTheTotalValuesByCategoryBetweenDate(anyLong(), any(), any(), any())).thenReturn(new BigDecimal("550.00"));
        when(partyService.getById(PARTY_ID)).thenReturn(party);

        AwardResultDTO result = awardService.finishSpendingLimitGoal(GOAL_ID);

        assertNotNull(result);
        assertEquals(-2, result.pointsCalculated());
        assertTrue(result.message().contains("Unfortunately"));
        verify(partyService).updatePoints(PARTY_ID, -2);
        verify(goalService).complete(GOAL_ID);
    }

    @Test
    void finishSpendingLimitGoal_PercentualType_Success_UnderBudget() {
        TransactionCategory shoppingCategory = new TransactionCategory();
        SearchedGoalDTO goal = new SearchedGoalDTO(
                GOAL_ID, "Limit Shopping", null, false, null, null, PARTY_ID,
                null, null, null, null,
                SpendingLimitGoalType.PERCENTUAL, null, new BigDecimal("20.00"), LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), shoppingCategory
        );
        when(goalService.getById(GOAL_ID)).thenReturn(goal);
        when(reportService.getTheTotalValuesByTypeBetweenDate(anyLong(), any(), any(), eq(TransactionType.INCOME))).thenReturn(new BigDecimal("3000.00"));
        when(reportService.getTheTotalValuesByCategoryBetweenDate(anyLong(), any(), any(), eq(shoppingCategory))).thenReturn(new BigDecimal("500.00"));
        when(partyService.getById(PARTY_ID)).thenReturn(party);

        AwardResultDTO result = awardService.finishSpendingLimitGoal(GOAL_ID);

        assertNotNull(result);
        assertEquals(2, result.pointsCalculated());
        assertTrue(result.message().contains("Congratulations"));
        verify(partyService).updatePoints(PARTY_ID, 2);
        verify(goalService).complete(GOAL_ID);
    }
}