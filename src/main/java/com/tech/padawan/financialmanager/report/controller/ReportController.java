package com.tech.padawan.financialmanager.report.controller;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByTypeDTO;
import com.tech.padawan.financialmanager.report.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
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

    @GetMapping("/saved-money-by-month")
    public ResponseEntity<List<SavedMoneyByMonth>> getSavedMoneyByMonth(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> initialDate,
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

    @GetMapping("/transaction-count-per-type")
    public ResponseEntity<List<TransactionCountByTypeDTO>> getMonthlyTransactionCountGroupedByType(@RequestHeader("Authorization") String authorizationHeader){
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        List<TransactionCountByTypeDTO> report = service.getMonthlyTransactionCountGroupedByType(id);
        return ResponseEntity.ok(report);
    }
}