package com.tech.padawan.financialmanager.party.service.exception;

import com.tech.padawan.financialmanager.global.exception.AlreadyExistsException;

public class ChampionAlreadyInThePartyException extends AlreadyExistsException {
    public ChampionAlreadyInThePartyException(String message) {
        super(message);
    }
}
