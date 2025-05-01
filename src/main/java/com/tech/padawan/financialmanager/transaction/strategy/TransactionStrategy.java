package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.user.model.User;

public interface TransactionStrategy {
    User apply(User user, double value);
    User revert(User user, double value);
}
