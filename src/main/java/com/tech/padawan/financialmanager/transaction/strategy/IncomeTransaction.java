package com.tech.padawan.financialmanager.transaction.strategy;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.user.model.User;

import java.math.BigDecimal;

public class IncomeTransaction implements TransactionStrategy{
    @Override
    public Party apply(Party party, BigDecimal value) {
        party.setBalance(party.getBalance().add(value));
        return party;
    }

    @Override
    public Party revert(Party party, BigDecimal value) {
        party.setBalance(party.getBalance().subtract(value));
        return party;
    }
}
