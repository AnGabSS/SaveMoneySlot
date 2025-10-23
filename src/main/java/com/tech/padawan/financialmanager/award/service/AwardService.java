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
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AwardService implements IAwardService{


    private final IGoalService goalService;
    private final ISavingGoalService savingGoalService;
    private final IPartyService partyService;
    private final IReportService reportService;

    private static final BigDecimal POINTS_CONVERSION_RATE = new BigDecimal("0.10");

    public AwardService(IGoalService goalService, IPartyService partyService, IReportService reportService, ISavingGoalService savingGoalService) {
        this.goalService = goalService;
        this.partyService = partyService;
        this.reportService = reportService;
        this.savingGoalService = savingGoalService;
    }

    @Override
    @Transactional
    public AwardResultDTO receiveSavingGoalAward(Long id) {
        SearchedGoalDTO goal = goalService.getById(id);
        if(goal.savedAmount().compareTo(goal.targetAmount()) < 0){
            throw new CannotReceiveTheAwardException("Saved amount is not enough to receive the goal award. Required: " + goal.targetAmount() + ", Saved: " + goal.savedAmount());
        }
        if(goal.isCompleted()){
            throw new CannotReceiveTheAwardException("Award already claimed");
        }
        BigDecimal averageIncomeOfTheParty = reportService.getTheMonthlyAverageValuesByType(goal.partyId(), TransactionType.INCOME);
        BigDecimal pointsTaxForTheParty = averageIncomeOfTheParty.multiply(POINTS_CONVERSION_RATE);
        Integer pointsToBeReceive = goal.targetAmount().divide(pointsTaxForTheParty, RoundingMode.DOWN).toBigInteger().intValue();
        partyService.updatePoints(goal.partyId(), pointsToBeReceive);
        Party party = partyService.getById(goal.partyId());
        goalService.complete(id);
        String message =  "Congratulations for reach this goal, you gain " + pointsToBeReceive + " points, now the party have " + party.getPoints();
        return new AwardResultDTO(message, pointsToBeReceive);
    }

    @Override
    public AwardResultDTO finishSpendingLimitGoal(Long id) {
        SearchedGoalDTO goal = goalService.getById(id);
        if(LocalDate.now().isBefore(goal.finalDate())){
            int remainingDays = LocalDate.now().compareTo(goal.finalDate());
            throw new CannotReceiveTheAwardException("You cannot complete this goal, there are still " + remainingDays + " days left until its end");
        }
        if(goal.isCompleted()){
            throw new CannotReceiveTheAwardException("Award already claimed");
        }

        int pointsToBeReceive = switch (goal.limitType()) {
            case PERCENTUAL -> updatePointForPercentualSpendingLimitGoal(goal);
            case AMOUNT -> updatePointForAmountSpendingLimitGoal(goal);
        };


        Party party = partyService.getById(goal.partyId());
        goalService.complete(id);
        String message = "Congratulations for reach this goal, you gain " + pointsToBeReceive + " points, now the party have " + party.getPoints();
        if(pointsToBeReceive > 0){
            message = "Congratulations for reach this goal, you gain " + pointsToBeReceive + " points, now the party have " + party.getPoints();
        } else {
            message = "Unfortunately you did not reach the goal and lost" + pointsToBeReceive + " points, now the party have " + party.getPoints();
        }
        return new AwardResultDTO(message, pointsToBeReceive);
    }


    public int updatePointForPercentualSpendingLimitGoal(SearchedGoalDTO goal){
        BigDecimal averageIncomeOfTheParty = reportService.getTheTotalValuesByTypeBetweenDate(
                goal.partyId(),
                goal.initialDate().atStartOfDay(),
                goal.finalDate().atTime(LocalTime.MAX),
                TransactionType.INCOME
        );
        BigDecimal averageExpenseOfTheParty = reportService.getTheTotalValuesByCategoryBetweenDate(
                goal.partyId(),
                goal.initialDate().atStartOfDay(),
                goal.finalDate().atTime(LocalTime.MAX),
                goal.category()
        );
        BigDecimal maxValue = averageIncomeOfTheParty.multiply(goal.limitPercentage().divide(BigDecimal.valueOf(100)));
        BigDecimal pointsDivisor = maxValue.multiply(POINTS_CONVERSION_RATE);
        int pointsToReceive = getPointsToReceive(pointsDivisor, averageExpenseOfTheParty, maxValue);
        partyService.updatePoints(goal.partyId(), pointsToReceive);
        return pointsToReceive;
    }
    
    public int updatePointForAmountSpendingLimitGoal(SearchedGoalDTO goal){
        BigDecimal averageExpenseOfTheParty = reportService.getTheTotalValuesByCategoryBetweenDate(
                goal.partyId(),
                goal.initialDate().atStartOfDay(),
                goal.finalDate().atTime(LocalTime.MAX),
                goal.category()
        );
        BigDecimal maxValue = goal.limitAmount();
        BigDecimal pointsDivisor = goal.limitAmount().multiply(POINTS_CONVERSION_RATE);
        int pointsToReceive = getPointsToReceive(pointsDivisor, averageExpenseOfTheParty, maxValue);
        partyService.updatePoints(goal.partyId(), pointsToReceive);
        return pointsToReceive;
    }

    private static int getPointsToReceive(BigDecimal pointsDivisor, BigDecimal averageExpenseOfTheParty, BigDecimal maxValue) {
        int pointsToReceive = 0;

        if (pointsDivisor.compareTo(BigDecimal.ZERO) > 0) {

            if (averageExpenseOfTheParty.compareTo(maxValue) < 0) {
                BigDecimal amountUnderLimit = maxValue.subtract(averageExpenseOfTheParty);
                pointsToReceive = amountUnderLimit.divide(pointsDivisor, RoundingMode.DOWN).intValue() + 1;

            } else {
                BigDecimal amountOverLimit = averageExpenseOfTheParty.subtract(maxValue);
                BigDecimal pointsToLose = amountOverLimit.divide(pointsDivisor, RoundingMode.DOWN);
                pointsToReceive = pointsToLose.negate().intValue() - 1;
            }
        }
        return pointsToReceive;
    }
}
