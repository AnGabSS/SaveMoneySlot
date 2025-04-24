package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;

import java.util.List;

public interface ITransactionService {
    List<Transaction> findAll();
    Transaction getById(Long id);
    Transaction create(CreateTransactionDTO transactionDTO);
    Transaction update(Long id, UpdateTransactionDTO transactionDTO);
    String delete(Long id);
}
