package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategy;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategyFactory;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TransactionService implements ITransactionService{
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private UserService userService;

    @Override
    public Page<SearchedTransactionDTO> findAll(int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<Transaction> list = repository.findAll(pageRequest);
        return list.map(SearchedTransactionDTO::from);
    }

    @Override
    public SearchedTransactionDTO getById(Long id) {
        Transaction transaction =  Optional.of(repository.findById(id)).get().orElseThrow(() -> new TransactionNotFound("Transaction with id " + id + " not found."));
        return SearchedTransactionDTO.from(transaction);
    }

    @Transactional
    @Override
    public Transaction create(CreateTransactionDTO transactionDTO) {
        User user = userService.getUserEntityById(transactionDTO.userId());

        TransactionStrategy strategy = TransactionStrategyFactory.getStrategy(transactionDTO.type());
        user = strategy.apply(user, transactionDTO.value());

        userService.updateUserCompleted(user);

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
    public SearchedTransactionDTO update(Long id, UpdateTransactionDTO transactionDTO) {
        Transaction transaction = repository.getReferenceById(id);

        User user = userService.getUserEntityById(transaction.getUser().getId());

        // Revert the old transaction value
        TransactionStrategy strategyForTheOldTransaction = TransactionStrategyFactory.getStrategy(transaction.getType());
        user = strategyForTheOldTransaction.revert(user, transaction.getValue());

        //Apply the new transaction value
        TransactionStrategy strategy = TransactionStrategyFactory.getStrategy(transactionDTO.type());
        user = strategy.apply(user, transactionDTO.value());

        userService.updateUserCompleted(user);

        transaction.setValue(transactionDTO.value());
        transaction.setDescription(transactionDTO.description());
        transaction.setType(transactionDTO.type());

        Transaction transactionUpdated = repository.save(transaction);
        return SearchedTransactionDTO.from(transactionUpdated);
    }

    @Override
    public String delete(Long id) {
        repository.deleteById(id);
        return "Transaction deleted";
    }

    @Override
    public Page<SearchedTransactionDTO> findAllByUser(long userid, int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<Transaction> list = repository.findAllByUserId(pageRequest, userid);
        return list.map(SearchedTransactionDTO::from);
    }

}
