package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategy;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategyFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionBalanceService implements ITransactionBalanceService{
    public Party applyTransaction(Party party, BigDecimal value, TransactionType type) {
        TransactionStrategy strategy = TransactionStrategyFactory.getStrategy(type);
        return strategy.apply(party, value);
    }

    public Party revertTransaction(Party party, BigDecimal value, TransactionType type) {
        TransactionStrategy strategy = TransactionStrategyFactory.getStrategy(type);
        return strategy.revert(party, value);
    }
}
