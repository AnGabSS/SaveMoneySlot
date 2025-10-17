package com.tech.padawan.financialmanager.goal.controller;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.goal.dto.CreateGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateSaveAmountDTO;
import com.tech.padawan.financialmanager.goal.model.Goal;
import com.tech.padawan.financialmanager.goal.service.GoalService;
import com.tech.padawan.financialmanager.goal.service.IGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@Tag(
        name = "Goals",
        description = "Endpoints for managing user's financial goals, such as saving for a trip or a new car."
)
public class GoalController {

    private final IGoalService service;
    private final JwtTokenService tokenService;

    public GoalController(IGoalService service, JwtTokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Get a paginated list of all goals",
            description = "Retrieves a list of all goals in the system, with support for pagination and sorting.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the page of goals.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<SearchedGoalDTO>> findAll(
            @Parameter(description = "The page number to retrieve, starting in page number 1.", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,

            @Parameter(description = "The number of results per page.")
            @RequestParam(value = "size", defaultValue = "4") int size,

            @Parameter(description = "The field to sort the results by (e.g., 'id', 'description').")
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,

            @Parameter(description = "The sort direction ('ASC' for ascending or 'DESC' for descending).")
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAll(page, size, orderBy,direction));
    }

    @Operation(
            summary = "Get a goal by ID",
            description = "Retrieves a single goal by its unique ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the goal.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedGoalDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Goal not found for the provided ID.", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SearchedGoalDTO> findById(
            @Parameter(description = "ID of the goal to be retrieved.", required = true, example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "Create a new goal",
            description = "Creates a new financial goal and associates it with a user.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Goal created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedGoalDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload. Check for missing or invalid fields.", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<SearchedGoalDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for the new goal to be created.", required = true,
                    content = @Content(schema = @Schema(implementation = CreateGoalDTO.class))
            )
            @RequestBody @Validated CreateGoalDTO goalDTO
    ){
        Goal goalCreated = service.create(goalDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(goalCreated.getId()).toUri();
        return ResponseEntity.created(uri).body(SearchedGoalDTO.from(goalCreated));
    }

    @Operation(
            summary = "Update an existing goal",
            description = "Updates the details of an existing goal by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Goal updated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedGoalDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Goal not found for the provided ID.", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<SearchedGoalDTO> update(
            @Parameter(description = "ID of the goal to be updated.", required = true, example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated data for the goal.", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateGoalDTO.class))
            )
            @RequestBody @Validated UpdateGoalDTO goalDTO
    ){
        return ResponseEntity.ok(service.update(id, goalDTO));
    }

    @Operation(
            summary = "Delete a goal",
            description = "Permanently deletes a goal by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Goal deleted successfully.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Goal deleted successfully"))
                    ),
                    @ApiResponse(responseCode = "404", description = "Goal not found for the provided ID.", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @Parameter(description = "ID of the goal to be deleted.", required = true, example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok(service.delete(id));
    }

    @Operation(
            summary = "Get all goals for a specific user",
            description = "Retrieves a paginated list of all goals belonging to a specific user, identified by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the user's goals.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found for the provided ID.", content = @Content)
            }
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<List<SearchedGoalDTO>> findAllByUser(
            @Parameter(description = "Authentication JWT token. Must be prefixed with 'Bearer '.", required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader("Authorization") String authorizationHeader,

            @Parameter(description = "The page number to retrieve, starting in page number 1.", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,

            @Parameter(description = "The number of results per page.")
            @RequestParam(value = "size", defaultValue = "4") int size,

            @Parameter(description = "The field to sort the results by.")
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,

            @Parameter(description = "The sort direction ('ASC' or 'DESC').")
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        return ResponseEntity.ok(service.findAllByUserId(id, page, size, orderBy, direction).getContent());
    }

    @Operation(
            summary = "Update the saved amount for a goal",
            description = "Updates only the 'saved amount' of a specific goal. This is useful for adding funds towards a goal.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Saved amount updated successfully.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid value provided.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Goal not found for the provided ID.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedGoalDTO.class)))
            }
    )
    @PutMapping("/{id}/saveamount")
    public ResponseEntity<SearchedGoalDTO> update(
            @Parameter(description = "ID of the goal to have its saved amount updated.", required = true, example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Object containing the new value to be added to the saved amount.", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateSaveAmountDTO.class))
            )
            @RequestBody @Valid UpdateSaveAmountDTO dto
    ) {
        return ResponseEntity.ok(service.updateSaveAmount(id, dto.value()));
    }

}
