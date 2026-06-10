package ru.volkov.cw.service;

public interface ReportService {
    void generateSuppliesReport();
    void generatePriceChangesReport();
    void generateInventoryValuation();
    void generateAuditSummary();
}
