package ru.volkov.cw.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ru.volkov.cw.dao.InventoryDAO;
import ru.volkov.cw.dao.ProductDAO;
import ru.volkov.cw.model.InventoryItem;
import ru.volkov.cw.model.Product;
import ru.volkov.cw.model.Store;
import java.util.function.Consumer;

public class InventoryController {
    @FXML private ComboBox<Store> cbStoreSelect;
    @FXML private TableView<InventoryItem> tableStoreInventory;
    @FXML private TableColumn<InventoryItem, String> colInventoryProduct;
    @FXML private TableColumn<InventoryItem, Integer> colInventoryQuantity, colInventoryMinLimit;
    @FXML private TableColumn<InventoryItem, String> colInventorySpecs;
    @FXML private Button btnAddToStore, btnUpdateQuantity;

    private final InventoryDAO invDao = new InventoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private ObservableList<InventoryItem> invData = FXCollections.observableArrayList();
    private Consumer<String> statusCallback;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

    @FXML public void initialize() {
        colInventoryProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colInventoryQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colInventoryMinLimit.setCellValueFactory(new PropertyValueFactory<>("minStock"));
        colInventorySpecs.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(""));

        btnAddToStore.setOnAction(e -> {
            Store s = cbStoreSelect.getSelectionModel().getSelectedItem();
            if (s != null) showInventoryDialog(s, null);
        });
        btnUpdateQuantity.setOnAction(e -> {
            Store s = cbStoreSelect.getSelectionModel().getSelectedItem();
            InventoryItem item = tableStoreInventory.getSelectionModel().getSelectedItem();
            if (s != null && item != null) showInventoryDialog(s, item);
        });
    }

    public void selectStore(Store s) { cbStoreSelect.getSelectionModel().select(s); }

    public void loadInventoryForStore(int storeId) {
        invData.clear(); invData.addAll(invDao.getItems(storeId)); tableStoreInventory.setItems(invData);
    }

    private void showInventoryDialog(Store s, InventoryItem item) {
        Dialog<Boolean> d = new Dialog<>();
        d.setTitle(item == null ? "Добавить" : "Обновить");
        javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane(); g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));

        ComboBox<Product> cbProd = new ComboBox<>();
        ObservableList<Product> allProds = FXCollections.observableArrayList(productDAO.getAllProducts());
        cbProd.setItems(allProds);
        cbProd.setConverter(new StringConverter<>() { @Override public String toString(Product p) { return p == null ? "" : p.getName(); } @Override public Product fromString(String str) { return null; } });

        TextField txtQty = new TextField(); TextField txtMin = new TextField();
        if (item != null) { cbProd.setDisable(true); cbProd.setValue(allProds.stream().filter(p -> p.getId() == item.getProductId()).findFirst().orElse(null)); txtQty.setText(String.valueOf(item.getQuantity())); txtMin.setText(String.valueOf(item.getMinStock())); }

        g.add(new Label("Товар:"), 0, 0); g.add(cbProd, 1, 0);
        g.add(new Label("Кол-во:"), 0, 1); g.add(txtQty, 1, 1);
        g.add(new Label("Мин:"), 0, 2); g.add(txtMin, 1, 2);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    int q = Integer.parseInt(txtQty.getText()); int m = Integer.parseInt(txtMin.getText());
                    if (item == null) return invDao.addProductToStore(s.getId(), cbProd.getValue().getId(), q, m);
                    else return invDao.updateInventory(s.getId(), item.getProductId(), q, m);
                } catch (Exception e) { return false; }
            } return false;
        });
        d.showAndWait().ifPresent(res -> { if (res) loadInventoryForStore(s.getId()); });
    }
}