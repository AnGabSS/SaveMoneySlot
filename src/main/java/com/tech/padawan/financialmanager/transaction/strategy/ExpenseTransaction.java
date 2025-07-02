package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;

import java.math.BigDecimal;

public class ExpenseTransaction implements TransactionStrategy{
    @Override
    public User apply(User user, BigDecimal value) {
        user.setBalance(user.getBalance().subtract(value));
        return user;
    }

    @Override
    public User revert(User user, BigDecimal value) {
        user.setBalance(user.getBalance().add(value));
        return user;
    }
}
