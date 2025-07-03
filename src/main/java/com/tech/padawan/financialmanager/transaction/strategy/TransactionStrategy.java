package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;

import java.math.BigDecimal;

public interface TransactionStrategy {
    User apply(User user, BigDecimal value);
    User revert(User user, BigDecimal value);
}
