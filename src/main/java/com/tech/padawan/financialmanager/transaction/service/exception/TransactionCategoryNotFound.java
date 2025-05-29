package com.tech.padawan.financialmanager.transaction.service.exception;

import com.tech.padawan.financialmanager.global.exception.NotFoundException;

public class TransactionCategoryNotFound extends NotFoundException {
    public TransactionCategoryNotFound(String message) {
        super(message);
    }
}
