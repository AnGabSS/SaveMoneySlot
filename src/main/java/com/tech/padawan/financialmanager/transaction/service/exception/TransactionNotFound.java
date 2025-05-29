package com.tech.padawan.financialmanager.transaction.service.exception;

import com.tech.padawan.financialmanager.global.exception.NotFoundException;

public class TransactionNotFound extends NotFoundException {
    public TransactionNotFound(String message) {
        super(message);
    }
}
