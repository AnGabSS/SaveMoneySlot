package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.repository.TransactionCategoryRepository;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionCategoryNotFound;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionCategoryService implements ITransactionCategoryService{
    private final TransactionCategoryRepository repository;
    private final IPartyService partyService;


    public TransactionCategoryService(
            TransactionCategoryRepository repository,
            IPartyService partyService
    ) {
        this.repository = repository;
        this.partyService = partyService;

    }

    @Override
    public Page<SearchedTransactionCategoryDTO> findAll(int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1 , size, Sort.Direction.valueOf(direction), orderBy);
        Page<TransactionCategory> list = repository.findAll(pageRequest);
        return list.map(SearchedTransactionCategoryDTO::from);
    }

    @Override
    public Page<SearchedTransactionCategoryDTO> findAllByPartyId(Long id, int page, int size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.Direction.valueOf(direction), orderBy);
        Page<TransactionCategory> list = repository.findAllByPartyId(pageRequest, id);
        return list.map(SearchedTransactionCategoryDTO::from);
    }

    @Override
    public SearchedTransactionCategoryDTO getById(Long id) {
        TransactionCategory category = Optional.of(repository.findById(id)).get().orElseThrow(() -> new TransactionCategoryNotFound("Transaction Category with id " + id + " not found."));
        return SearchedTransactionCategoryDTO.from(category);
    }

    @Override
    public TransactionCategory create(CreateTransactionCategoryDTO transactionDTO) {
        Party party = partyService.getById(transactionDTO.partyId());
        TransactionCategory category = TransactionCategory.builder()
                .name(transactionDTO.name())
                .type(transactionDTO.type())
                .party(party)
                .build();

        return repository.save(category);
    }

    @Override
    public SearchedTransactionCategoryDTO update(Long id, UpdateTransactionCategoryDTO transactionDTO) {
        TransactionCategory oldCategory = repository.getReferenceById(id);

        oldCategory.setName(transactionDTO.name());
        oldCategory.setType(transactionDTO.type());

        TransactionCategory newCategory = repository.save(oldCategory);
        return SearchedTransactionCategoryDTO.from(newCategory);
    }

    @Override
    public String delete(Long id) {
        this.getById(id);
        repository.deleteById(id);
        return "Transaction category deleted";
    }

    @Override
    public TransactionCategory getEntityById(Long id) {
        TransactionCategory category = Optional.of(repository.findById(id)).get().orElseThrow(() -> new TransactionCategoryNotFound("Transaction Category with id " + id + " not found."));
        return category;
    }
}
