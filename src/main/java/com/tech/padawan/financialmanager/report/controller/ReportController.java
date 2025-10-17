package com.tech.padawan.financialmanager.report.controller;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByTypeDTO;
import com.tech.padawan.financialmanager.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(
        name = "Reports",
        description = "Endpoints for generating financial reports based on user transaction data."
)
public class ReportController {

    private final ReportService service;
    private final JwtTokenService tokenService;

    private static final LocalDateTime DEFAULT_INITIAL_DATE = LocalDateTime.of(1900, 1, 1, 0, 0);;


    public ReportController(
            ReportService service,
            JwtTokenService tokenService){
        this.service = service;
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Get money saved by month",
            description = "Calculates and retrieves a report of the total money saved for each month within a specified date range. The user is identified by the JWT token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the saved money report.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SavedMoneyByMonth.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request. This can occur if the 'initialDate' is after the 'finalDate'.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Token is invalid or missing.",
                            content = @Content
                    )
            }
    )
    @GetMapping("/saved-money-by-month")
    public ResponseEntity<List<SavedMoneyByMonth>> getSavedMoneyByMonth(
            @Parameter(description = "Authentication JWT token.", required = true)
            @RequestHeader("Authorization") String authorizationHeader,

            @Parameter(description = "The start date for the report period (format yyyy-MM-dd). Defaults to a very early date if not provided.")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> initialDate,

            @Parameter(description = "The end date for the report period (format yyyy-MM-dd). Defaults to the current date if not provided.")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> finalDate
    ) {
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        LocalDateTime effectiveInitialDate = initialDate
                .map(LocalDate::atStartOfDay)
                .orElse(DEFAULT_INITIAL_DATE);

        LocalDateTime effectiveFinalDate = finalDate
                .map(date -> date.atTime(LocalTime.MAX))
                .orElse(LocalDateTime.now());

        if (effectiveInitialDate.isAfter(effectiveFinalDate)) {
            return ResponseEntity.badRequest().build();
        }

        List<SavedMoneyByMonth> report = service.getSavedMoneyByMonth(id, effectiveInitialDate, effectiveFinalDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Get transaction count by type",
            description = "Retrieves a report of the total number of transactions grouped by their type (e.g., income, expense) for the current month for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the transaction count report.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionCountByTypeDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Token is invalid or missing.",
                            content = @Content
                    )
            }
    )
    @GetMapping("/transaction-count-per-type")
    public ResponseEntity<List<TransactionCountByTypeDTO>> getMonthlyTransactionCountGroupedByType(
            @Parameter(description = "Authentication JWT token.", required = true)
            @RequestHeader("Authorization") String authorizationHeader
    ){
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        List<TransactionCountByTypeDTO> report = service.getMonthlyTransactionCountGroupedByType(id);
        return ResponseEntity.ok(report);
    }
}