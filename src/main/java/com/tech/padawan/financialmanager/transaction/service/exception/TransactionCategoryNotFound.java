package com.tech.padawan.financialmanager.transaction.service.exception;

public class TransactionCategoryNotFound extends RuntimeException {
    public TransactionCategoryNotFound(String message) {
        super(message);
    }
}
