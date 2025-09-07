package com.tech.padawan.financialmanager.report.service;

import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IReportService {
    List<SavedMoneyByMonth> getSavedMoneyByMonth(Long userId, Date initialDate, Date finalDate);
}
