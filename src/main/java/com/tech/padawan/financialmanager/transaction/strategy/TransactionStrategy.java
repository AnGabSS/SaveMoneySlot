package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;

import java.math.BigDecimal;

public interface TransactionStrategy {
    Party apply(Party party, BigDecimal value);
    Party revert(Party party, BigDecimal value);
}
