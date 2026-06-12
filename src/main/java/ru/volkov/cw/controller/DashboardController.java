package ru.volkov.cw.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import ru.volkov.cw.service.InventoryService;
import ru.volkov.cw.service.ReportService;

import java.io.File;
import java.time.LocalDate;
import java.util.function.Consumer;

public class DashboardController {
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalStoresLabel;

    // DatePickers для отчёта по поставкам
    @FXML private DatePicker dpSuppliesFrom;
    @FXML private DatePicker dpSuppliesTo;

    // DatePickers для отчёта по ценам
    @FXML private DatePicker dpPriceFrom;
    @FXML private DatePicker dpPriceTo;

    private InventoryService inventoryService;
    private ReportService reportService;
    private Consumer<String> statusCallback;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        loadInventoryStatus();
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

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

    @FXML
    private void handleSuppliesReport() {
        if (reportService == null) {
            showError("Ошибка", "Сервис отчётов не инициализирован");
            return;
        }

        LocalDate fromDate = dpSuppliesFrom.getValue();
        LocalDate toDate = dpSuppliesTo.getValue();

        if (fromDate == null || toDate == null) {
            showError("Ошибка ввода дат", "Пожалуйста, выберите дату начала и дату окончания периода");
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showError("Ошибка ввода дат", "Дата начала не может быть позже даты окончания");
            return;
        }

        try {
            // Открываем диалог сохранения файла
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить отчёт по поставкам");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("Отчёт_по_поставкам_" +
                    fromDate + "_по_" + toDate + ".xlsx");

            File file = fileChooser.showSaveDialog(dpSuppliesFrom.getScene().getWindow());

            if (file != null) {
                reportService.generateSuppliesReportExcel(fromDate, toDate, file);
                updateStatus("Отчёт по поставкам сохранён: " + file.getName());
                showSuccess("Отчёт создан", "Отчёт по поставкам успешно сгенерирован:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showError("Ошибка генерации отчёта", "Не удалось создать отчёт: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePriceChangesReport() {
        if (reportService == null) {
            showError("Ошибка", "Сервис отчётов не инициализирован");
            return;
        }

        LocalDate fromDate = dpPriceFrom.getValue();
        LocalDate toDate = dpPriceTo.getValue();

        if (fromDate == null || toDate == null) {
            showError("Ошибка ввода дат", "Пожалуйста, выберите дату начала и дату окончания периода");
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showError("Ошибка ввода дат", "Дата начала не может быть позже даты окончания");
            return;
        }

        try {
            // Открываем диалог сохранения файла
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить отчёт по изменению цен");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("Отчёт_по_ценам_" +
                    fromDate + "_по_" + toDate + ".xlsx");

            File file = fileChooser.showSaveDialog(dpPriceFrom.getScene().getWindow());

            if (file != null) {
                reportService.generatePriceChangesReportExcel(fromDate, toDate, file);
                updateStatus("Отчёт по ценам сохранён: " + file.getName());
                showSuccess("Отчёт создан", "Отчёт по изменению цен успешно сгенерирован:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showError("Ошибка генерации отчёта", "Не удалось создать отчёт: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatus(String msg) {
        if (statusCallback != null) statusCallback.accept(msg);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}