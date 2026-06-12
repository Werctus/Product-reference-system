package ru.volkov.cw.service;

import java.io.File;
import java.time.LocalDate;

public interface ReportService {
    void generateSuppliesReport();
    void generatePriceChangesReport();
    void generateInventoryValuation();
    void generateAuditSummary();

    // Новые методы для Excel-отчётов
    void generateSuppliesReportExcel(LocalDate fromDate, LocalDate toDate, File file);
    void generatePriceChangesReportExcel(LocalDate fromDate, LocalDate toDate, File file);
}