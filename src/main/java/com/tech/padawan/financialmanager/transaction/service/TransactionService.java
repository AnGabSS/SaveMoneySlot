package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements ITransactionService{
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<SearchedTransactionDTO> findAll() {
        List<Transaction> list = repository.findAll();
        return list.stream().map(SearchedTransactionDTO::from).toList();
    }

    @Override
    public SearchedTransactionDTO getById(Long id) {
        Transaction transaction =  Optional.of(repository.findById(id)).get().orElseThrow(() -> new TransactionNotFound("Transaction with id " + id + " not found."));
        return SearchedTransactionDTO.from(transaction);
    }

    @Override
    public Transaction create(CreateTransactionDTO transactionDTO) {
        User user = userRepository.getReferenceById(transactionDTO.userId());
        Transaction transaction = Transaction.builder()
                .value(transactionDTO.value())
                .description(transactionDTO.description())
                .type(transactionDTO.type())
                .createdAt(new Date())
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
