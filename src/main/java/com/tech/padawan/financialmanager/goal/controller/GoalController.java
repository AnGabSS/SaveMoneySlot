package com.tech.padawan.financialmanager.goal.controller;

import com.tech.padawan.financialmanager.goal.dto.*;
import com.tech.padawan.financialmanager.goal.model.Goal;
import com.tech.padawan.financialmanager.goal.model.SavingGoal;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoal;
import com.tech.padawan.financialmanager.goal.service.IGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/goals")
@Tag(name = "Goals", description = "Endpoints for managing user's financial goals.")
public class GoalController {

    private final IGoalService service;

    public GoalController(IGoalService service) {
        this.service = service;
    }


    @Operation(summary = "Create a new Saving Goal", responses = {
            @ApiResponse(responseCode = "201", description = "Saving Goal created successfully.", content = @Content(schema = @Schema(implementation = SavingGoal.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content)
    })
    @PostMapping("/saving")
    public ResponseEntity<SavingGoal> createSavingGoal(@Valid @RequestBody CreateSavingGoalDTO dto) {
        SavingGoal createdGoal = service.createSavingGoal(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdGoal.getId()).toUri();
        return ResponseEntity.created(uri).body(createdGoal);
    }

    @Operation(summary = "Create a new Spending Limit Goal", responses = {
            @ApiResponse(responseCode = "201", description = "Spending Limit Goal created successfully.", content = @Content(schema = @Schema(implementation = SpendingLimitGoal.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content)
    })
    @PostMapping("/spending-limit")
    public ResponseEntity<SpendingLimitGoal> createSpendingLimitGoal(@Valid @RequestBody CreateSpendingLimitGoalDTO dto) {
        SpendingLimitGoal createdGoal = service.createSpendingLimitGoal(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdGoal.getId()).toUri();
        return ResponseEntity.created(uri).body(createdGoal);
    }


    @Operation(summary = "Update an existing Saving Goal", responses = {
            @ApiResponse(responseCode = "200", description = "Saving Goal updated successfully.", content = @Content(schema = @Schema(implementation = SearchedGoalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Saving Goal not found.", content = @Content)
    })
    @PutMapping("/saving/{id}")
    public ResponseEntity<SearchedGoalDTO> updateSavingGoal(@PathVariable Long id, @Valid @RequestBody UpdateSavingGoalDTO dto) {
        return ResponseEntity.ok(service.updateSavingGoal(id, dto));
    }

    @Operation(summary = "Update an existing Spending Limit Goal", responses = {
            @ApiResponse(responseCode = "200", description = "Spending Limit Goal updated successfully.", content = @Content(schema = @Schema(implementation = SearchedGoalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Spending Limit Goal not found.", content = @Content)
    })
    @PutMapping("/spending-limit/{id}")
    public ResponseEntity<SearchedGoalDTO> updateSpendingLimitGoal(@PathVariable Long id, @Valid @RequestBody UpdateSpendingLimitGoalDTO dto) {
        return ResponseEntity.ok(service.updateSpendingLimitGoal(id, dto));
    }

    @Operation(summary = "Update the saved amount for a Saving Goal", responses = {
            @ApiResponse(responseCode = "200", description = "Saved amount updated successfully.", content = @Content(schema = @Schema(implementation = SearchedGoalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Saving Goal not found.", content = @Content)
    })
    @PatchMapping("/saving/{id}/saved-amount")
    public ResponseEntity<SearchedGoalDTO> updateSavedAmount(@PathVariable Long id, @Valid @RequestBody UpdateSaveAmountDTO dto) {
        return ResponseEntity.ok(service.updateSaveAmount(id, dto.value()));
    }


    @Operation(summary = "Get a goal by ID (any type)", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the goal.", content = @Content(schema = @Schema(implementation = SearchedGoalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Goal not found.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SearchedGoalDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Get all goals for a specific party (paginated)", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the party's goals.", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/by-party/{partyId}")
    public ResponseEntity<Page<SearchedGoalDTO>> findAllByParty(
            @PathVariable Long partyId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAllByPartyId(partyId, page, size, orderBy, direction));
    }

    @Operation(summary = "Delete a goal by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Goal deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Goal not found.", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}