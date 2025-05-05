package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import org.springframework.data.domain.Page;

public interface ITransactionService {
    Page<SearchedTransactionDTO> findAll(int page, int size, String orderBy, String direction);
    SearchedTransactionDTO getById(Long id);
    Transaction create(CreateTransactionDTO transactionDTO);
    SearchedTransactionDTO update(Long id, UpdateTransactionDTO transactionDTO);
    String delete(Long id);
    Page<SearchedTransactionDTO> findAllByUser(long userid, int page, int size, String orderBy, String direction);
}
