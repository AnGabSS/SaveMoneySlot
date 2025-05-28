package com.tech.padawan.financialmanager.transaction.controller;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.service.ITransactionCategoryService;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionCategoryNotFound;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
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
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (TransactionCategoryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid CreateTransactionCategoryDTO categoryDTO) {
        try {
            TransactionCategory category = service.create(categoryDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(category.getId()).toUri();
            return ResponseEntity.created(uri).body(SearchedTransactionCategoryDTO.from(category));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Valid UpdateTransactionCategoryDTO categoryDTO) {
        try {
            SearchedTransactionCategoryDTO newCategory = service.update(id, categoryDTO);
            return ResponseEntity.ok(newCategory);
        } catch (TransactionCategoryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.delete(id));
        } catch (TransactionCategoryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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
