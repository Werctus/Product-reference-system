package ru.volkov.cw.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import ru.volkov.cw.service.InventoryService;
import ru.volkov.cw.service.ReportService;
import java.util.function.Consumer;

public class DashboardController {
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalStoresLabel;

    private InventoryService inventoryService;
    private ReportService reportService;
    private Consumer<String> statusCallback;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        loadInventoryStatus();
    }

    public void setReportService(ReportService reportService) { this.reportService = reportService; }

    private void loadInventoryStatus() {
        if (inventoryService == null) return;
        try {
            totalProductsLabel.setText(String.valueOf(inventoryService.getTotalProductsCount()));
            lowStockLabel.setText(String.valueOf(inventoryService.getLowStockProductsCount()));
            totalStoresLabel.setText(String.valueOf(inventoryService.getTotalStoresCount()));
        } catch (Exception e) {
            showError("Ошибка загрузки данных", "Не удалось получить статус инвентаря: " + e.getMessage());
        }
    }

    @FXML private void handleSuppliesReport() { if (reportService != null) reportService.generateSuppliesReport(); }
    @FXML private void handlePriceChangesReport() { if (reportService != null) reportService.generatePriceChangesReport(); }
    @FXML private void handleInventoryValuation() { if (reportService != null) reportService.generateInventoryValuation(); }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }
}