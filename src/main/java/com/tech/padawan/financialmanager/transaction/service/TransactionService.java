package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.party.model.Party;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements ITransactionService{

    private final TransactionRepository repository;
    private final IPartyService partyService;
    private final ITransactionBalanceService balanceService;
    private final ITransactionCategoryService categoryService;

    public TransactionService(
            TransactionRepository repository,
            IPartyService partyService,
            ITransactionBalanceService balanceService,
            ITransactionCategoryService categoryService
    ) {
        this.repository = repository;
        this.partyService = partyService;
        this.balanceService = balanceService;
        this.categoryService = categoryService;
    }


    @Override
    public Page<SearchedTransactionDTO> findAllByPartyId(Long id, String description, int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.Direction.valueOf(direction), orderBy);
        Page<Transaction> list = repository.findAllByPartyIdAndDescriptionContainingIgnoreCase(pageRequest, id, description);
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
        Party party = partyService.getById(transactionDTO.partyId());
        TransactionCategory category = categoryService.getEntityById(transactionDTO.category());

        party = balanceService.applyTransaction(party, transactionDTO.value(), category.getType());

        partyService.updateCompleted(party);

        Transaction transaction = Transaction.builder()
                .value(transactionDTO.value())
                .description(transactionDTO.description())
                .category(category)
                .createdAt(LocalDateTime.now())
                .party(party)
                .build();

        return repository.save(transaction);
    }


    @Override
    public SearchedTransactionDTO update(Long id, UpdateTransactionDTO transactionDTO) {
        Transaction transaction = repository.getReferenceById(id);

        Party party = partyService.getById(transaction.getParty().getId());
        TransactionCategory category = categoryService.getEntityById(transactionDTO.categoryId());

        // Revert the old transaction value
        party = balanceService.revertTransaction(party, transaction.getValue(), transaction.getCategory().getType());

        //Apply the new transaction value
        party = balanceService.applyTransaction(party, transactionDTO.value(), category.getType());

        partyService.updateCompleted(party);

        transaction.setValue(transactionDTO.value());
        transaction.setDescription(transactionDTO.description());
        transaction.setCategory(category);

        Transaction transactionUpdated = repository.save(transaction);
        return SearchedTransactionDTO.from(transactionUpdated);
    }

    @Override
    public String delete(Long id) {
        this.getById(id);
        repository.deleteById(id);
        return "Transaction deleted";
    }

    @Override
    public List<Transaction> findAllByPartyIdAndMonth(Long id, LocalDateTime initialDate, LocalDateTime finalDate) {
        return repository.findAllByPartyIdAndCreatedAtBetween(id, initialDate, finalDate);
    }

    @Override
    public List<Transaction> findAllByPartyIdAndMonthAndType(Long id, LocalDateTime initialDate, LocalDateTime finalDate, TransactionType type) {
        return repository.findAllByPartyIdAndCreatedAtBetweenAndCategoryType(id, initialDate, finalDate, type);
    }

    @Override
    public List<Transaction> findAllByPartyIdAndMonthAndCategory(Long id, LocalDateTime initialDate, LocalDateTime finalDate, TransactionCategory category) {
        return repository.findAllByPartyIdAndCreatedAtBetweenAndCategory(id, initialDate, finalDate, category);
    }

    @Override
    public List<Transaction> findAllByPartyIdAndType(Long id, TransactionType type) {
        return repository.findAllByPartyIdAndCategoryType(id, type);
    }

}
