package com.tech.padawan.financialmanager.transaction.controller;

import com.tech.padawan.financialmanager.global.exception.NotFoundException;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.service.ITransactionCategoryService;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionCategoryNotFound;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transaction/category")
@Tag(
        name = "Transaction Categories",
        description = "Responsible for defining the different types of transactions, such as an expense with a car or money received from a freelance job."
)
public class TransactionCategoryController {

    @Autowired
    private ITransactionCategoryService service;

    @GetMapping
    public ResponseEntity<List<SearchedTransactionCategoryDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(service.findAll(page, size, orderBy, direction).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SearchedTransactionCategoryDTO> findById(@PathVariable Long id) {
            return ResponseEntity.ok(service.getById(id));

    }

    @PostMapping
    public ResponseEntity<SearchedTransactionCategoryDTO> create(@RequestBody @Valid CreateTransactionCategoryDTO categoryDTO) {
            TransactionCategory category = service.create(categoryDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(category.getId()).toUri();
            return ResponseEntity.created(uri).body(SearchedTransactionCategoryDTO.from(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SearchedTransactionCategoryDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateTransactionCategoryDTO categoryDTO) {
            SearchedTransactionCategoryDTO newCategory = service.update(id, categoryDTO);
            return ResponseEntity.ok(newCategory);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
            return ResponseEntity.ok(service.delete(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<SearchedTransactionCategoryDTO>> findAllByUser(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(service.findAllByUser(id, page, size, orderBy, direction).getContent());
    }
}
