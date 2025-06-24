package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;

public interface TransactionStrategy {
    User apply(User user, double value, IUserService service);
    User revert(User user, double value);
}
