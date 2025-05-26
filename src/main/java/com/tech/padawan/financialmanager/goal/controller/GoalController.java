package com.tech.padawan.financialmanager.goal.controller;

import com.tech.padawan.financialmanager.goal.dto.CreateGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateSaveAmountDTO;
import com.tech.padawan.financialmanager.goal.model.Goal;
import com.tech.padawan.financialmanager.goal.service.IGoalService;
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private IGoalService service;

    @GetMapping
    public ResponseEntity<Page<SearchedGoalDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAll(page, size, orderBy,direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id){
        try{
            return ResponseEntity.ok(service.getById(id));
        } catch (GoalNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<SearchedGoalDTO> create(@RequestBody @Validated CreateGoalDTO goalDTO){
        Goal goalCreated = service.create(goalDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(goalCreated.getId()).toUri();
        return ResponseEntity.created(uri).body(SearchedGoalDTO.from(goalCreated));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Validated UpdateGoalDTO goalDTO){
        try{
            return ResponseEntity.ok(service.update(id, goalDTO));
        } catch (UserNotFoundException | GoalNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        try{
            return ResponseEntity.ok(service.delete(id));
        } catch (GoalNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<SearchedGoalDTO>> findAllByUser(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAllByUserId(id, page, size, orderBy, direction).getContent());
    }

    @PutMapping("/{id}/saveamount")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Valid UpdateSaveAmountDTO dto){
        try{
            return ResponseEntity.ok(service.updateSaveAmount(id, dto.value()));
        } catch (UserNotFoundException | GoalNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
