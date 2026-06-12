package ru.volkov.cw.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ru.volkov.cw.dao.*;
import ru.volkov.cw.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class DeliveryController {
    @FXML private TableColumn<Delivery, String> colDelNum, colDelStatus;
    @FXML private TableColumn<Delivery, Integer> colDelComp, colDelStore;
    @FXML private TableColumn<Delivery, LocalDateTime> colDelDate;
    @FXML private TableView<Delivery> tableDeliveries;
    @FXML private Button btnAddDelivery, btnEditDelivery, btnDeleteDelivery;
    @FXML private TableView<DeliveryItem> tableDeliveryItems;
    @FXML private TableColumn<DeliveryItem, String> colDeliveryItemProduct;
    @FXML private TableColumn<DeliveryItem, Integer> colDeliveryItemQuantity;
    @FXML private TableColumn<DeliveryItem, BigDecimal> colDeliveryItemPrice, colDeliveryItemTotal;
    @FXML private Label lblDeliveryTotal;
    @FXML private Button btnAddDeliveryItem, btnEditDeliveryItem, btnDeleteDeliveryItem;
    @FXML private ResourceBundle resources;

    private final CompanyDAO companyDAO = new CompanyDAO();
    private final StoreDAO storeDao = new StoreDAO();
    private final DeliveryDAO delDao = new DeliveryDAO();
    private final DeliveryItemDAO itemDao = new DeliveryItemDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private ObservableList<Delivery> delData = FXCollections.observableArrayList();
    private ObservableList<DeliveryItem> delItemsData = FXCollections.observableArrayList();
    private Map<Integer, String> companyMap = new HashMap<>();
    private Map<Integer, String> storeMap = new HashMap<>();
    private Consumer<String> statusCallback;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

    @FXML public void initialize() { initDeliveryTab(); }

    private void initDeliveryTab() {
        loadDeliveryDictionaries();
        colDelNum.setCellValueFactory(new PropertyValueFactory<>("documentNumber"));
        colDelStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDelDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colDelDate.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(LocalDateTime d, boolean empty) { super.updateItem(d, empty); setText(empty || d == null ? null : d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))); } });
        colDelComp.setCellValueFactory(new PropertyValueFactory<>("companyId"));
        colDelComp.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(Integer id, boolean empty) { super.updateItem(id, empty); setText(empty || id == null ? null : companyMap.getOrDefault(id, "?")); } });
        colDelStore.setCellValueFactory(new PropertyValueFactory<>("storeId"));
        colDelStore.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(Integer id, boolean empty) { super.updateItem(id, empty); setText(empty || id == null ? null : storeMap.getOrDefault(id, "?")); } });

        colDeliveryItemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colDeliveryItemQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDeliveryItemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDeliveryItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tableDeliveries.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if (n != null) loadDeliveryItems(n.getId()); else delItemsData.clear(); });

        btnAddDeliveryItem.setOnAction(e -> { Delivery d = tableDeliveries.getSelectionModel().getSelectedItem(); if (d != null) showDelItemDialog(d, null); });
        btnDeleteDeliveryItem.setOnAction(e -> {
            Delivery d = tableDeliveries.getSelectionModel().getSelectedItem(); DeliveryItem item = tableDeliveryItems.getSelectionModel().getSelectedItem();
            if (d != null && item != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && itemDao.deleteItem(d.getId(), item.getProductId())) loadDeliveryItems(d.getId()); });
            }
        });

        btnAddDelivery.setOnAction(e -> {
            Dialog<Delivery> d = new Dialog<>(); d.setTitle("Новая поставка");
            ButtonType saveBtn = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE); d.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
            javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane(); g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));
            ComboBox<Company> cbComp = new ComboBox<>(); cbComp.getItems().addAll(companyDAO.getAllCompanies()); cbComp.setConverter(new StringConverter<>() { @Override public String toString(Company c) { return c == null ? "" : c.getName(); } @Override public Company fromString(String s) { return null; } });
            ComboBox<Store> cbStore = new ComboBox<>(); cbStore.getItems().addAll(storeDao.getAllStores()); cbStore.setConverter(new StringConverter<>() { @Override public String toString(Store s) { return s == null ? "" : s.getName(); } @Override public Store fromString(String s) { return null; } });
            g.add(new Label("Фирма:"), 0, 0); g.add(cbComp, 1, 0); g.add(new Label("Магазин:"), 0, 1); g.add(cbStore, 1, 1);
            d.getDialogPane().setContent(g);
            d.setResultConverter(b -> b == saveBtn ? new Delivery("", cbComp.getValue().getId(), cbStore.getValue().getId()) : null);
            d.showAndWait().ifPresent(del -> { if (delDao.addDelivery(del) != -1) { loadDelData(); updateStatus("Создано"); } });
        });

        btnDeleteDelivery.setOnAction(e -> {
            Delivery sel = tableDeliveries.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && delDao.deleteDelivery(sel.getId())) { delData.remove(sel); updateStatus("Удалено"); } });
            }
        });
        loadDelData();
    }

    private void loadDeliveryDictionaries() {
        companyMap.clear(); for (Company c : companyDAO.getAllCompanies()) companyMap.put(c.getId(), c.getName());
        storeMap.clear(); for (Store s : storeDao.getAllStores()) storeMap.put(s.getId(), s.getName());
    }

    private void loadDeliveryItems(int id) {
        delItemsData.clear(); delItemsData.addAll(itemDao.getItemsByDelivery(id)); tableDeliveryItems.setItems(delItemsData);
        BigDecimal sum = BigDecimal.ZERO; for (DeliveryItem i : delItemsData) if (i.getTotal() != null) sum = sum.add(i.getTotal());
        lblDeliveryTotal.setText(sum.toString());
    }

    private void showDelItemDialog(Delivery d, DeliveryItem editItem) {
        Dialog<DeliveryItem> dialog = new Dialog<>(); dialog.setTitle("Товар поставки");
        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE); dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane(); g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));
        ComboBox<Product> cbProd = new ComboBox<>(); cbProd.getItems().addAll(productDAO.getAllProducts()); cbProd.setConverter(new StringConverter<>() { @Override public String toString(Product p) { return p == null ? "" : p.getName(); } @Override public Product fromString(String s) { return null; } });
        TextField tQty = new TextField("1"); TextField tPrice = new TextField("0");
        if (editItem != null) { cbProd.setDisable(true); cbProd.setValue(cbProd.getItems().stream().filter(p -> p.getId() == editItem.getProductId()).findFirst().orElse(null)); tQty.setText(String.valueOf(editItem.getQuantity())); tPrice.setText(editItem.getPrice().toString()); }
        g.add(new Label("Товар:"), 0, 0); g.add(cbProd, 1, 0); g.add(new Label("Кол-во:"), 0, 1); g.add(tQty, 1, 1); g.add(new Label("Цена:"), 0, 2); g.add(tPrice, 1, 2);
        dialog.getDialogPane().setContent(g);
        dialog.setResultConverter(b -> {
            if (b == saveBtn) {
                Product p = cbProd.getValue(); if (p != null) {
                    DeliveryItem item = new DeliveryItem(); item.setDeliveryId(d.getId()); item.setProductId(p.getId());
                    item.setQuantity(Integer.parseInt(tQty.getText())); item.setPrice(new BigDecimal(tPrice.getText().replace(",", "."))); return item;
                }
            } return null;
        });
        dialog.showAndWait().ifPresent(item -> { if (editItem != null ? itemDao.updateItem(item) : itemDao.addItem(item)) loadDeliveryItems(d.getId()); });
    }

    private void loadDelData() { delData.clear(); delData.addAll(delDao.getAllDeliveries()); tableDeliveries.setItems(delData); }
    private void updateStatus(String msg) { if (statusCallback != null) statusCallback.accept(msg); }
}