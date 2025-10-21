package com.tech.padawan.financialmanager.award.exceptions;

import com.tech.padawan.financialmanager.global.exception.BusinessRuleException;

public class CannotReceiveTheAwardException extends BusinessRuleException {
    public CannotReceiveTheAwardException(String message) {
        super(message);
    }
}
