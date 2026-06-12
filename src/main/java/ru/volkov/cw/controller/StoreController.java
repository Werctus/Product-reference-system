package ru.volkov.cw.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.volkov.cw.dao.StoreDAO;
import ru.volkov.cw.model.Store;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class StoreController {
    @FXML private TableView<Store> tableStores;
    @FXML private TableColumn<Store, String> colStoreName, colStorePhone, colStoreAddress, colStoreEmail;
    @FXML private Button btnAddStore, btnEditStore, btnDeleteStore;

    private final StoreDAO storeDao = new StoreDAO();
    private ObservableList<Store> storeData = FXCollections.observableArrayList();
    private InventoryController inventoryController;
    private Consumer<String> statusCallback;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }
    public void setInventoryController(InventoryController inventoryController) { this.inventoryController = inventoryController; }

    @FXML public void initialize() {
        colStoreName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStorePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colStoreAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStoreEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableStores.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null && inventoryController != null) {
                inventoryController.selectStore(n);
                inventoryController.loadInventoryForStore(n.getId());
            }
        });

        btnAddStore.setOnAction(e -> showStoreDialog(null).ifPresent(s -> { if (storeDao.addStore(s)) { loadStoresData(); updateStatus("Магазин добавлен"); } }));
        btnEditStore.setOnAction(e -> {
            Store sel = tableStores.getSelectionModel().getSelectedItem();
            if (sel != null) showStoreDialog(sel).ifPresent(s -> { if (storeDao.updateStore(s)) { tableStores.refresh(); updateStatus("Обновлено"); } });
        });
        btnDeleteStore.setOnAction(e -> {
            Store sel = tableStores.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && storeDao.deleteStore(sel.getId())) { storeData.remove(sel); updateStatus("Удалено"); } });
            }
        });
        loadStoresData();
    }

    private void loadStoresData() { storeData.clear(); storeData.addAll(storeDao.getAllStores()); tableStores.setItems(storeData); }

    private Optional<Store> showStoreDialog(Store editStore) {
        Dialog<Store> d = new Dialog<>();
        d.setTitle(editStore == null ? "Добавить" : "Ред.");
        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane(); g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));

        TextField txtName = new TextField(); TextField txtAddr = new TextField();
        TextField txtPhone = new TextField(); txtPhone.setTextFormatter(new TextFormatter<>(ch -> ch.getControlNewText().matches("\\d{0,10}") ? ch : null));
        TextField txtEmail = new TextField();

        if (editStore != null) { txtName.setText(editStore.getName()); txtAddr.setText(editStore.getAddress()); txtPhone.setText(editStore.getPhone().substring(2)); txtEmail.setText(editStore.getEmail()); }

        g.add(new Label("Название:"), 0, 0); g.add(txtName, 1, 0);
        g.add(new Label("Телефон:"), 0, 1); g.add(txtPhone, 1, 1);
        g.add(new Label("Адрес:"), 0, 2); g.add(txtAddr, 1, 2);
        g.add(new Label("Email:"), 0, 3); g.add(txtEmail, 1, 3);
        d.getDialogPane().setContent(g);

        d.setResultConverter(b -> {
            if (b == saveBtn) {
                Store s = editStore == null ? new Store() : editStore;
                s.setName(txtName.getText()); s.setAddress(txtAddr.getText()); s.setPhone("+7" + txtPhone.getText()); s.setEmail(txtEmail.getText());
                return s;
            } return null;
        });
        return d.showAndWait();
    }
    private void updateStatus(String msg) { if (statusCallback != null) statusCallback.accept(msg); }
}