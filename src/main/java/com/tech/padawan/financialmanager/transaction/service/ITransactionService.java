package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionService {
    Page<SearchedTransactionDTO> findAllByPartyId(Long id, String search, int page, int size, String orderBy, String direction);
    SearchedTransactionDTO getById(Long id);
    Transaction create(CreateTransactionDTO transactionDTO);
    SearchedTransactionDTO update(Long id, UpdateTransactionDTO transactionDTO);
    String delete(Long id);
    List<Transaction> findAllByPartyIdAndMonth(Long id, LocalDateTime initialDate, LocalDateTime finalDate);
    List<Transaction> findAllByPartyIdAndMonthAndType(Long id, LocalDateTime initialDate, LocalDateTime finalDate, TransactionType type);
    List<Transaction> findAllByPartyIdAndMonthAndCategory(Long id, LocalDateTime initialDate, LocalDateTime finalDate, TransactionCategory category);
    List<Transaction> findAllByPartyIdAndType(Long id, TransactionType type);
}
