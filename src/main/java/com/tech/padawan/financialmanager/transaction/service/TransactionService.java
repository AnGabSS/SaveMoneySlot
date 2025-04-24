package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;

public class TransactionService implements ITransactionService{

    private TransactionRepository repository;
    private UserRepository userRepository;

    @Override
    public List<Transaction> findAll() {
        return repository.findAll();
    }

    @Override
    public Transaction getById(Long id) {
        return Optional.of(repository.findById(id)).get().orElseThrow(() -> new TransactionNotFound("Transaction with id " + id + " not found."));
    }

    @Override
    public Transaction create(CreateTransactionDTO transactionDTO) {
        User user = userRepository.getReferenceById(transactionDTO.userId());
        Transaction transaction = Transaction.builder()
                .value(transactionDTO.value())
                .description(transactionDTO.description())
                .type(transactionDTO.type())
                .user(user)
                .build();
        return repository.save(transaction);
    }

    @Override
    public Transaction update(Long id, UpdateTransactionDTO transactionDTO) {
        Transaction transaction = repository.getReferenceById(id);
        transaction.setValue(transactionDTO.value());
        transaction.setDescription(transactionDTO.description());
        transaction.setType(transactionDTO.type());
        return repository.save(transaction);
    }

    @Override
    public String delete(Long id) {
        repository.deleteById(id);
        return "Transaction deleted";
    }
}
