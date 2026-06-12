package ru.volkov.cw.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.volkov.cw.dao.BrandDAO;
import ru.volkov.cw.dao.UnitOfMeasureDAO;
import ru.volkov.cw.model.Brand;
import ru.volkov.cw.model.UnitOfMeasure;
import java.util.function.Consumer;

public class ReferencesController {
    @FXML private ListView<Brand> listBrands;
    @FXML private TextField txtBrandName;
    @FXML private Button btnAddBrand, btnDeleteBrand;
    @FXML private ListView<UnitOfMeasure> listUnits;
    @FXML private TextField txtUnitName;
    @FXML private Button btnAddUnit, btnDeleteUnit;

    private final BrandDAO brandDAO = new BrandDAO();
    private final UnitOfMeasureDAO unitDAO = new UnitOfMeasureDAO();
    private Consumer<String> statusCallback;
    private Runnable onDataChanged;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }
    public void setOnDataChanged(Runnable onDataChanged) { this.onDataChanged = onDataChanged; }

    @FXML public void initialize() {
        listBrands.setCellFactory(param -> new ListCell<>() { @Override protected void updateItem(Brand b, boolean empty) { super.updateItem(b, empty); setText(empty || b == null ? null : b.getName()); } });
        listUnits.setCellFactory(param -> new ListCell<>() { @Override protected void updateItem(UnitOfMeasure u, boolean empty) { super.updateItem(u, empty); setText(empty || u == null ? null : u.getName()); } });

        btnAddBrand.setOnAction(e -> { if (brandDAO.addBrand(new Brand(txtBrandName.getText().trim()))) { txtBrandName.clear(); loadReferencesData(); notifyChanged(); } });
        btnDeleteBrand.setOnAction(e -> {
            Brand sel = listBrands.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить бренд?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && brandDAO.deleteBrand(sel.getId())) { loadReferencesData(); notifyChanged(); } });
            }
        });

        btnAddUnit.setOnAction(e -> { if (unitDAO.addUnit(new UnitOfMeasure(txtUnitName.getText().trim()))) { txtUnitName.clear(); loadReferencesData(); notifyChanged(); } });
        btnDeleteUnit.setOnAction(e -> {
            UnitOfMeasure sel = listUnits.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить ед. изм.?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && unitDAO.deleteUnit(sel.getId())) { loadReferencesData(); notifyChanged(); } });
            }
        });
        loadReferencesData();
    }

    private void loadReferencesData() {
        listBrands.getItems().clear(); listBrands.getItems().addAll(brandDAO.getAllBrands());
        listUnits.getItems().clear(); listUnits.getItems().addAll(unitDAO.getAllUnits());
    }

    private void notifyChanged() {
        if (onDataChanged != null) onDataChanged.run();
        if (statusCallback != null) statusCallback.accept("Справочники обновлены");
    }
}