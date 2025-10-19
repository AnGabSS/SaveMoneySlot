package com.tech.padawan.financialmanager.party.controller;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.party.dto.ChangeChampionInPartyDTO;
import com.tech.padawan.financialmanager.party.dto.CreateUpdatePartyDTO;
import com.tech.padawan.financialmanager.party.dto.SearchedPartyDTO;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
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
@RequestMapping("/api/v1/party")
@Tag(
        name = "Party",
        description = "Create party for start your save money journey"
)
public class PartyController {

    private final IPartyService service;

    private final JwtTokenService tokenService;

    public PartyController(IPartyService service, JwtTokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Get user's parties",
            description = "Retrieves a paginated list of all parties belonging to the authenticated user. The list can be filtered by a search term.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the page of parties.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. Token is invalid or missing.", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<Page<SearchedPartyDTO>> findAll(
            @Parameter(description = "Authentication JWT token.", required = true)
            @RequestHeader("Authorization") String authorizationHeader,

            @Parameter(description = "Search term to filter partys by description.")
            @RequestParam(value = "search", defaultValue = "") String search,

            @Parameter(description = "The page number to retrieve, starting in page number 1.", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,

            @Parameter(description = "The number of results per page.")
            @RequestParam(value = "size", defaultValue = "10") int size,

            @Parameter(description = "The field to sort the results by.")
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,

            @Parameter(description = "The sort direction ('ASC' or 'DESC').")
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        return ResponseEntity.ok(service.findAllByUserId(id, search, page, size, orderBy, direction));
    }

    @Operation(
            summary = "Get a party by ID",
            description = "Retrieves a single party by its unique ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the party.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedPartyDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Party not found for the provided ID.", content = @Content)
            }
    )
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<SearchedPartyDTO> findById(
            @Parameter(description = "ID of the party to be retrieved.", required = true, example = "1") @PathVariable Long id
    ){
        return ResponseEntity.ok(service.getByIdFormatted(id));
    }

    @Operation(
            summary = "Create a new party",
            description = "Creates a new party for the authenticated user. The user is identified via the JWT token.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Party created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedPartyDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. Token is invalid or missing.", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<SearchedPartyDTO> create(
            @Parameter(description = "Authentication JWT token.", required = true)
            @RequestHeader("Authorization") String authorizationHeader,

            @RequestBody @Valid CreateUpdatePartyDTO partyDTO
            ){
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        Party party = service.create(id, partyDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(party.getId()).toUri();
        return ResponseEntity.created(uri).body(SearchedPartyDTO.from(party));
    }


    @Operation(
            summary = "Update an existing party",
            description = "Updates the details of an existing party by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Party updated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedPartyDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Party not found for the provided ID.", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<SearchedPartyDTO> update(
            @Parameter(description = "ID of the party to be updated.", required = true, example = "1")
            @PathVariable Long id,

            @RequestBody @Valid CreateUpdatePartyDTO partyDTO
            ){
        SearchedPartyDTO party = service.update(id, partyDTO);
        return ResponseEntity.ok(party);
    }


    @Operation(
            summary = "Add Champion to an existing party",
            description = "Add Champion to an existing party by its ID and the nickname of champion.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Party updated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedPartyDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Party not found for the provided ID.", content = @Content)
            }
    )
    @PatchMapping("/{id}/add-champion")
    public ResponseEntity<SearchedPartyDTO> addChampion(
            @Parameter(description = "ID of the party to be updated.", required = true, example = "1")
            @PathVariable Long id,

            @RequestBody @Valid ChangeChampionInPartyDTO dto
    ){
        SearchedPartyDTO party = service.addChampion(id, dto);
        return ResponseEntity.ok(party);
    }

    @Operation(
            summary = "Remove Champion to an existing party",
            description = "Remove Champion to an existing party by its ID and the nickname of champion.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Party updated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedPartyDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Party not found for the provided ID.", content = @Content)
            }
    )
    @PatchMapping("/{id}/remove-champion")
    public ResponseEntity<SearchedPartyDTO> removeChampion(
            @Parameter(description = "ID of the party to be updated.", required = true, example = "1")
            @PathVariable Long id,

            @RequestBody @Valid ChangeChampionInPartyDTO dto
    ){
        SearchedPartyDTO party = service.removeChampion(id, dto);
        return ResponseEntity.ok(party);
    }

    @Operation(
            summary = "Delete a party",
            description = "Permanently deletes a party by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Party deleted successfully.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(responseCode = "404", description = "Party not found for the provided ID.", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @Parameter(description = "ID of the party to be deleted.", required = true, example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok(service.delete(id));
    }

}
