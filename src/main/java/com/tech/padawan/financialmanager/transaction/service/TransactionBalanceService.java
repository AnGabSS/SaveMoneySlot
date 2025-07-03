package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategy;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategyFactory;
import com.tech.padawan.financialmanager.user.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionBalanceService implements ITransactionBalanceService{
    public User applyTransaction(User user, BigDecimal value, TransactionType type) {
        TransactionStrategy strategy = TransactionStrategyFactory.getStrategy(type);
        return strategy.apply(user, value);
    }

    public User revertTransaction(User user, BigDecimal value, TransactionType type) {
        TransactionStrategy strategy = TransactionStrategyFactory.getStrategy(type);
        return strategy.revert(user, value);
    }
}
