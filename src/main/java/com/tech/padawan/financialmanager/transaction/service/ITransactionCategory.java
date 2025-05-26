package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import org.springframework.data.domain.Page;

public interface ITransactionCategory {
    Page<TransactionCategory> findAll(int page, int size, String orderBy, String direction);
    TransactionCategory getById(Long id);
    Transaction create(CreateTransactionCategoryDTO transactionDTO);
    TransactionCategory update(Long id, CreateTransactionCategoryDTO transactionDTO);
    String delete(Long id);
    Page<TransactionCategory> findAllByUser(long userid, int page, int size, String orderBy, String direction);
}