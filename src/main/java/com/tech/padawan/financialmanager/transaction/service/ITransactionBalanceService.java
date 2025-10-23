package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;

import java.math.BigDecimal;

public interface ITransactionBalanceService {
    Party applyTransaction(Party party, BigDecimal value, TransactionType type);
    Party revertTransaction(Party party, BigDecimal value, TransactionType type);
}
