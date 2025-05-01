package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;

public class TransactionStrategyFactory {

    public static TransactionStrategy getStrategy(TransactionType type){
        return switch (type){
            case INCOME -> new IncomeTransaction();
            case EXPENSE -> new ExpenseTransaction();
            case INVESTMENT -> new InvestmentTransaction();
        };
    }
}
