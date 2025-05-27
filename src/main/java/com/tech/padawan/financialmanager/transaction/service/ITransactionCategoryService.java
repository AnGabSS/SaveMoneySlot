package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import org.springframework.data.domain.Page;

public interface ITransactionCategoryService {
    Page<SearchedTransactionCategoryDTO> findAll(int page, int size, String orderBy, String direction);
    SearchedTransactionCategoryDTO getById(Long id);
    TransactionCategory create(CreateTransactionCategoryDTO transactionDTO);
    SearchedTransactionCategoryDTO update(Long id, UpdateTransactionCategoryDTO transactionDTO);
    String delete(Long id);
    Page<SearchedTransactionCategoryDTO> findAllByUser(long userid, int page, int size, String orderBy, String direction);
}