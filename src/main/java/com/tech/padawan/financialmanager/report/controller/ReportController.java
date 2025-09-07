package com.tech.padawan.financialmanager.report.controller;

import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    private static final Date DEFAULT_INITIAL_DATE;

    static {
        try {
            DEFAULT_INITIAL_DATE = new SimpleDateFormat("yyyy-MM-dd").parse("1900-01-01");
        } catch (ParseException e) {
            throw new IllegalStateException("Não foi possível parsear a data padrão inicial.", e);
        }
    }


    public ReportController(ReportService service){
        this.service = service;
    }

    @GetMapping("/saved-money-by-month/{userId}")
    public ResponseEntity<List<SavedMoneyByMonth>> getSavedMoneyByMonth(
            @PathVariable("userId") long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> initialDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> finalDate
    ) {
        Date effectiveInitialDate = initialDate.orElse(DEFAULT_INITIAL_DATE);
        Date effectiveFinalDate = finalDate.orElse(new Date());

        if (effectiveInitialDate.after(effectiveFinalDate)) {
            return ResponseEntity.badRequest().build();
        }

        List<SavedMoneyByMonth> report = service.getSavedMoneyByMonth(userId, effectiveInitialDate, effectiveFinalDate);
        return ResponseEntity.ok(report);
    }
}