package com.tech.padawan.financialmanager.party.service.exception;

import com.tech.padawan.financialmanager.global.exception.NotFoundException;

public class PartyNotFoundException extends NotFoundException {
    public PartyNotFoundException(String message) {
        super(message);
    }
}
