package com.tech.padawan.financialmanager.award.controller;

import com.tech.padawan.financialmanager.award.dto.AwardResultDTO; // Import the DTO
import com.tech.padawan.financialmanager.award.service.IAwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/awards")
@Tag(
        name = "Awards", // Corrected Tag
        description = "Endpoints for claiming awards from completed goals."
)
public class AwardController {

    private final IAwardService service;

    public AwardController(IAwardService service) {
        this.service = service;
    }

    @Operation(
            summary = "Claim the award for a Saving Goal",
            description = """
                    Endpoint to calculate and receive points after completing a **Saving Goal**.
                    The goal must be 100% saved.
                    <br><br>
                    **Points Formula:**
                    `Points = (Goal's Target Amount) / (10% of Average Monthly Income)`
                    <br>
                    *The greater the goal relative to your income, the greater the reward.*
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Award claimed successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AwardResultDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "The goal is not yet complete.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Goal not found for the provided ID.", content = @Content)
            }
    )
    @PatchMapping("/{id}/saving-goal/claim-award")
    public ResponseEntity<AwardResultDTO> receiveSavingGoalAward(
            @Parameter(description = "ID of the Saving Goal to be claimed.", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.receiveSavingGoalAward(id));
    }

    @Operation(
            summary = "Finish a Spending Limit Goal",
            description = """
                    Endpoint to finish a **Spending Limit Goal** and calculate the points earned or lost.
                    <br><br>
                    ### ✅ **Success Scenario (Spending < Limit):**
                    * **+1 base point** for achieving the goal.
                    * **+1 bonus point** for every **10%** of the limit that you saved.
                    * *E.g., If the limit was $500 and you spent $400, you saved $100 (20%). You earn +1 (base) +2 (bonus) = **3 points**.*
                    
                    ### ❌ **Failure Scenario (Spending > Limit):**
                    * **-1 base point** for failing the goal.
                    * **-1 penalty point** for every **10%** of the limit that you exceeded.
                    * *E.g., If the limit was $500 and you spent $550, you exceeded it by $50 (10%). You receive -1 (base) -1 (penalty) = **-2 points**.*
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Goal finished and points calculated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AwardResultDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Goal not found for the provided ID.", content = @Content)
            }
    )
    @PatchMapping("/{id}/spending-limit-goal/finish")
    public ResponseEntity<AwardResultDTO> finishSpendingLimitGoal(
            @Parameter(description = "ID of the Spending Limit Goal to be finished.", required = true, example = "1")
            @PathVariable Long id
    ) {
        // Assuming your service now returns the AwardResultDTO
        return ResponseEntity.ok(service.finishSpendingLimitGoal(id));
    }
}