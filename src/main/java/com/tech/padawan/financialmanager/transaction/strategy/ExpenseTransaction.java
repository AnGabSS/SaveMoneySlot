package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;

public class ExpenseTransaction implements TransactionStrategy{
    @Override
    public User apply(User user, double value, IUserService service) {
        user.setBalance(user.getBalance() - value);
        return user;
    }

    @Override
    public User revert(User user, double value) {
        user.setBalance(user.getBalance() + value);
        return user;
    }
}
