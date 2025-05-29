package com.tech.padawan.financialmanager.transaction.controller;

import com.tech.padawan.financialmanager.global.exception.NotFoundException;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.service.ITransactionService;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private ITransactionService service;

    @GetMapping
    public ResponseEntity<List<SearchedTransactionDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAll(page, size, orderBy, direction).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SearchedTransactionDTO> findById(@PathVariable Long id){
            return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<SearchedTransactionDTO> create(@RequestBody @Valid CreateTransactionDTO transactionDTO){
            Transaction transaction = service.create(transactionDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transaction.getId()).toUri();
            return ResponseEntity.created(uri).body(SearchedTransactionDTO.from(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SearchedTransactionDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateTransactionDTO transactionDTO){
            SearchedTransactionDTO newTransation = service.update(id, transactionDTO);
            return ResponseEntity.ok(newTransation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
            return ResponseEntity.ok(service.delete(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<SearchedTransactionDTO>> findAllByUser(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAllByUser(id, page, size, orderBy, direction).getContent());
    }
}

