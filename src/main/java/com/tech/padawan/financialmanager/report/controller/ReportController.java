package com.tech.padawan.financialmanager.report.controller;

import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByTypeDTO;
import com.tech.padawan.financialmanager.report.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    private static final LocalDateTime DEFAULT_INITIAL_DATE = LocalDateTime.of(1900, 1, 1, 0, 0);;


    public ReportController(ReportService service){
        this.service = service;
    }

    @GetMapping("/saved-money-by-month/{userId}")
    public ResponseEntity<List<SavedMoneyByMonth>> getSavedMoneyByMonth(
            @PathVariable("userId") long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDateTime> initialDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDateTime> finalDate
    ) {
        LocalDateTime effectiveInitialDate = initialDate.orElse(DEFAULT_INITIAL_DATE);
        LocalDateTime effectiveFinalDate = finalDate.orElse(LocalDateTime.now());

        if (effectiveInitialDate.isAfter(effectiveFinalDate)) {
            return ResponseEntity.badRequest().build();
        }

        List<SavedMoneyByMonth> report = service.getSavedMoneyByMonth(userId, effectiveInitialDate, effectiveFinalDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/transaction-count-per-type/{userId}")
    public ResponseEntity<List<TransactionCountByTypeDTO>> getMonthlyTransactionCountGroupedByType(@PathVariable("userId") long userId){
        List<TransactionCountByTypeDTO> report = service.getMonthlyTransactionCountGroupedByType(userId);
        return ResponseEntity.ok(report);
    }
}