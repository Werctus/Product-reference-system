package ru.volkov.cw.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import util.LocalizationManager;
import ru.volkov.cw.service.InventoryService;
import ru.volkov.cw.service.ReportService;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainController {
    @FXML private ComboBox<String> cbLanguage;
    @FXML private TabPane mainTabPane;
    @FXML private Label lblStatusBar;

    // Инжекция дочерних контроллеров (имена должны совпадать с fx:id в <fx:include>)
    @FXML private DashboardController dashboardController;
    @FXML private CompanyController companyController;
    @FXML private ProductController productController;
    @FXML private ReferencesController referencesController;
    @FXML private CategoryController categoryController;
    @FXML private StoreController storeController;
    @FXML private InventoryController inventoryController;
    @FXML private DeliveryController deliveryController;
    @FXML private PriceHistoryController priceHistoryController;

    @FXML private ResourceBundle resources;

    private InventoryService inventoryService;
    private ReportService reportService;

    @FXML
    public void initialize() {
        initLanguageSwitcher();
        initStatusBar();

        // Передаем callback для обновления статус-бара во все контроллеры
        Consumer<String> statusCallback = this::setStatus;
        if (dashboardController != null) dashboardController.setStatusCallback(statusCallback);
        if (companyController != null) companyController.setStatusCallback(statusCallback);
        if (productController != null) productController.setStatusCallback(statusCallback);
        if (referencesController != null) referencesController.setStatusCallback(statusCallback);
        if (categoryController != null) categoryController.setStatusCallback(statusCallback);
        if (storeController != null) storeController.setStatusCallback(statusCallback);
        if (inventoryController != null) inventoryController.setStatusCallback(statusCallback);
        if (deliveryController != null) deliveryController.setStatusCallback(statusCallback);
        if (priceHistoryController != null) priceHistoryController.setStatusCallback(statusCallback);

        // Настраиваем связь между контроллерами
        if (referencesController != null && productController != null) {
            referencesController.setOnDataChanged(productController::loadDictionaries);
        }
        if (categoryController != null && productController != null) {
            categoryController.setOnDataChanged(() -> {
                productController.loadMainCats();
                productController.refreshCategoryFilters();
            });
        }
        if (storeController != null && inventoryController != null) {
            storeController.setInventoryController(inventoryController);
        }
    }

    public void setStatus(String msg) {
        if (lblStatusBar != null) lblStatusBar.setText(msg);
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        if (dashboardController != null) dashboardController.setInventoryService(inventoryService);
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
        if (dashboardController != null) dashboardController.setReportService(reportService);
    }

    private void initLanguageSwitcher() {
        if (cbLanguage != null) {
            cbLanguage.getItems().addAll("RU Русский", "EN English", "PL Polski");
            Locale current = LocalizationManager.getCurrentLocale();
            if ("pl".equals(current.getLanguage())) cbLanguage.getSelectionModel().select(2);
            else if ("en".equals(current.getLanguage())) cbLanguage.getSelectionModel().select(1);
            else cbLanguage.getSelectionModel().select(0);
            cbLanguage.setOnAction(e -> handleChangeLanguage());
        }
    }

    private void initStatusBar() {
        if (lblStatusBar != null) lblStatusBar.setText(LocalizationManager.getString("status.ready"));
    }

    private void handleChangeLanguage() {
        int idx = cbLanguage.getSelectionModel().getSelectedIndex();
        switch (idx) {
            case 1 -> LocalizationManager.setLocale("en", "US");
            case 2 -> LocalizationManager.setLocale("pl", "PL");
            default -> LocalizationManager.setLocale("ru", "RU");
        }
        int activeTabIndex = -1;
        if (mainTabPane != null) activeTabIndex = mainTabPane.getSelectionModel().getSelectedIndex();
        reloadApplication(activeTabIndex);
    }

    private void reloadApplication(int savedTabIndex) {
        try {
            Stage currentStage = (Stage) cbLanguage.getScene().getWindow();
            double width = currentStage.getWidth(), height = currentStage.getHeight();
            double x = currentStage.getX(), y = currentStage.getY();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"), LocalizationManager.getBundle());
            Parent root = loader.load();

            TabPane newTabPane = (TabPane) loader.getNamespace().get("mainTabPane");
            if (newTabPane != null && savedTabIndex != -1) newTabPane.getSelectionModel().select(savedTabIndex);

            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setWidth(width); currentStage.setHeight(height);
            currentStage.setX(x); currentStage.setY(y);
            currentStage.setTitle(LocalizationManager.getString("app.title"));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error"); alert.setHeaderText("Failed to reload"); alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}