package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.user.model.User;

import java.math.BigDecimal;

public interface ITransactionBalanceService {
    User applyTransaction(User user, BigDecimal value, TransactionType type);
    User revertTransaction(User user, BigDecimal value, TransactionType type);
}
