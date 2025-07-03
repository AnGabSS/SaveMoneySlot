package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.user.model.User;

import java.math.BigDecimal;

public class InvestmentTransaction implements TransactionStrategy{
    @Override
    public User apply(User user, BigDecimal value) {
        user.setBalance(user.getBalance().add(value));
        return user;

    }

    @Override
    public User revert(User user, BigDecimal value) {
        user.setBalance(user.getBalance().subtract(value));
        return user;
    }
}
