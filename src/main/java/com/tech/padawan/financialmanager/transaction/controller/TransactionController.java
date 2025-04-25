package com.tech.padawan.financialmanager.transaction.controller;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.service.TransactionService;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
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
    private TransactionService service;

    @GetMapping
    public ResponseEntity<List<SearchedTransactionDTO>> findAll(){
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id){
        try{
            return ResponseEntity.ok(service.getById(id));
        } catch (TransactionNotFound e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<SearchedTransactionDTO> create(@RequestBody CreateTransactionDTO transactionDTO){
        Transaction transaction = service.create(transactionDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transaction.getId()).toUri();
        return ResponseEntity.created(uri).body(SearchedTransactionDTO.from(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, @RequestBody UpdateTransactionDTO transactionDTO){
        Transaction newTransation = service.update(id, transactionDTO);
        return ResponseEntity.ok(newTransation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(service.delete(id));
    }
}

