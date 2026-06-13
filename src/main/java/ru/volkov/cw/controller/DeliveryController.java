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
import util.LocalizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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
    @FXML private Button btnChangeStatus;

    private final CompanyDAO companyDAO = new CompanyDAO();
    private final StoreDAO storeDao = new StoreDAO();
    private final DeliveryDAO delDao = new DeliveryDAO();
    private final DeliveryItemDAO itemDao = new DeliveryItemDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ReportDAO reportDAO = new ReportDAO(); // Для проведения поставки
    private ObservableList<Delivery> delData = FXCollections.observableArrayList();
    private ObservableList<DeliveryItem> delItemsData = FXCollections.observableArrayList();
    private Map<Integer, String> companyMap = new HashMap<>();
    private Map<Integer, String> storeMap = new HashMap<>();
    private Consumer<String> statusCallback;

    // Константы статусов
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

    @FXML public void initialize() { initDeliveryTab(); }

    private void initDeliveryTab() {
        loadDeliveryDictionaries();
        colDelNum.setCellValueFactory(new PropertyValueFactory<>("documentNumber"));

        colDelStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDelStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Локализуем статус
                    setText(LocalizationManager.getString("delivery.status." + status));

                    // Цветовая индикация статуса
                    if (STATUS_COMPLETED.equals(status)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                    }
                }
            }
        });

        colDelDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colDelDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null : d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            }
        });

        colDelComp.setCellValueFactory(new PropertyValueFactory<>("companyId"));
        colDelComp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : companyMap.getOrDefault(id, "?"));
            }
        });

        colDelStore.setCellValueFactory(new PropertyValueFactory<>("storeId"));
        colDelStore.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : storeMap.getOrDefault(id, "?"));
            }
        });

        colDeliveryItemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colDeliveryItemQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDeliveryItemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDeliveryItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tableDeliveries.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                loadDeliveryItems(n.getId());
                updateChangeStatusButton(n);
            } else {
                delItemsData.clear();
                if (btnChangeStatus != null) btnChangeStatus.setDisable(true);
            }
        });

        btnAddDeliveryItem.setOnAction(e -> {
            Delivery d = tableDeliveries.getSelectionModel().getSelectedItem();
            if (d != null) {
                if (STATUS_COMPLETED.equals(d.getStatus())) {
                    showAlert(Alert.AlertType.WARNING, "Внимание", "Нельзя изменять позиции проведенной поставки!");
                    return;
                }
                showDelItemDialog(d, null);
            }
        });

        btnEditDeliveryItem.setOnAction(e -> {
            Delivery d = tableDeliveries.getSelectionModel().getSelectedItem();
            DeliveryItem item = tableDeliveryItems.getSelectionModel().getSelectedItem();
            if (d != null && item != null) {
                if (STATUS_COMPLETED.equals(d.getStatus())) {
                    showAlert(Alert.AlertType.WARNING, "Внимание", "Нельзя изменять позиции проведенной поставки!");
                    return;
                }
                showDelItemDialog(d, item);
            } else {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите позицию для редактирования!");
            }
        });

        btnDeleteDeliveryItem.setOnAction(e -> {
            Delivery d = tableDeliveries.getSelectionModel().getSelectedItem();
            DeliveryItem item = tableDeliveryItems.getSelectionModel().getSelectedItem();
            if (d != null && item != null) {
                if (STATUS_COMPLETED.equals(d.getStatus())) {
                    showAlert(Alert.AlertType.WARNING, "Внимание", "Нельзя удалять позиции проведенной поставки!");
                    return;
                }
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.YES && itemDao.deleteItem(d.getId(), item.getProductId())) {
                        loadDeliveryItems(d.getId());
                    }
                });
            }
        });

        btnAddDelivery.setOnAction(e -> {
            Dialog<Delivery> d = new Dialog<>();
            d.setTitle("Новая поставка");
            ButtonType saveBtn = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
            d.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
            javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane();
            g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));
            ComboBox<Company> cbComp = new ComboBox<>();
            cbComp.getItems().addAll(companyDAO.getAllCompanies());
            cbComp.setConverter(new StringConverter<>() {
                @Override public String toString(Company c) { return c == null ? "" : c.getName(); }
                @Override public Company fromString(String s) { return null; }
            });
            ComboBox<Store> cbStore = new ComboBox<>();
            cbStore.getItems().addAll(storeDao.getAllStores());
            cbStore.setConverter(new StringConverter<>() {
                @Override public String toString(Store s) { return s == null ? "" : s.getName(); }
                @Override public Store fromString(String s) { return null; }
            });
            g.add(new Label("Фирма:"), 0, 0); g.add(cbComp, 1, 0);
            g.add(new Label("Магазин:"), 0, 1); g.add(cbStore, 1, 1);
            d.getDialogPane().setContent(g);
            d.setResultConverter(b -> b == saveBtn ? new Delivery("", cbComp.getValue().getId(), cbStore.getValue().getId()) : null);
            d.showAndWait().ifPresent(del -> {
                if (delDao.addDelivery(del) != -1) {
                    loadDelData();
                    updateStatus("Создано");
                }
            });
        });

        btnEditDelivery.setOnAction(e -> {
            Delivery sel = tableDeliveries.getSelectionModel().getSelectedItem();
            if (sel != null) {
                if (STATUS_COMPLETED.equals(sel.getStatus())) {
                    showAlert(Alert.AlertType.WARNING, "Внимание", "Нельзя редактировать проведенную поставку!");
                    return;
                }
                Dialog<Delivery> d = new Dialog<>();
                d.setTitle("Редактировать поставку");
                ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
                d.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
                javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane();
                g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));
                ComboBox<Company> cbComp = new ComboBox<>();
                cbComp.getItems().addAll(companyDAO.getAllCompanies());
                cbComp.setConverter(new StringConverter<>() {
                    @Override public String toString(Company c) { return c == null ? "" : c.getName(); }
                    @Override public Company fromString(String s) { return null; }
                });
                ComboBox<Store> cbStore = new ComboBox<>();
                cbStore.getItems().addAll(storeDao.getAllStores());
                cbStore.setConverter(new StringConverter<>() {
                    @Override public String toString(Store s) { return s == null ? "" : s.getName(); }
                    @Override public Store fromString(String s) { return null; }
                });

                cbComp.setValue(companyDAO.getAllCompanies().stream()
                        .filter(c -> c.getId() == sel.getCompanyId()).findFirst().orElse(null));
                cbStore.setValue(storeDao.getAllStores().stream()
                        .filter(s -> s.getId() == sel.getStoreId()).findFirst().orElse(null));

                g.add(new Label("Фирма:"), 0, 0); g.add(cbComp, 1, 0);
                g.add(new Label("Магазин:"), 0, 1); g.add(cbStore, 1, 1);
                d.getDialogPane().setContent(g);
                d.setResultConverter(b -> {
                    if (b == saveBtn && cbComp.getValue() != null && cbStore.getValue() != null) {
                        sel.setCompanyId(cbComp.getValue().getId());
                        sel.setStoreId(cbStore.getValue().getId());
                        return sel;
                    }
                    return null;
                });
                d.showAndWait();
                loadDelData();
            } else {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите поставку для редактирования!");
            }
        });

        btnDeleteDelivery.setOnAction(e -> {
            Delivery sel = tableDeliveries.getSelectionModel().getSelectedItem();
            if (sel != null) {
                if (STATUS_COMPLETED.equals(sel.getStatus())) {
                    showAlert(Alert.AlertType.WARNING, "Внимание", "Нельзя удалить проведенную поставку!");
                    return;
                }
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.YES && delDao.deleteDelivery(sel.getId())) {
                        delData.remove(sel);
                        updateStatus("Удалено");
                    }
                });
            }
        });

        // ✅ ОБРАБОТЧИК КНОПКИ СМЕНЫ СТАТУСА
        btnChangeStatus.setOnAction(e -> handleChangeStatus());

        loadDelData();
    }

    /**
     * Обновляет текст и состояние кнопки смены статуса
     */
    private void updateChangeStatusButton(Delivery delivery) {
        if (delivery == null || btnChangeStatus == null) return;

        if (STATUS_PENDING.equals(delivery.getStatus())) {
            btnChangeStatus.setText(LocalizationManager.getString("delivery.status.completed"));
            btnChangeStatus.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            btnChangeStatus.setDisable(false);
        } else if (STATUS_COMPLETED.equals(delivery.getStatus())) {
            btnChangeStatus.setText(LocalizationManager.getString("delivery.status.completed"));
            btnChangeStatus.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
            btnChangeStatus.setDisable(true);
        } else {
            btnChangeStatus.setDisable(true);
        }
    }

    /**
     * Обработчик смены статуса поставки
     */
    private void handleChangeStatus() {
        Delivery sel = tableDeliveries.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Внимание",
                    LocalizationManager.getString("delivery.selectFirst"));
            return;
        }

        String currentStatus = sel.getStatus();

        if (STATUS_PENDING.equals(currentStatus)) {
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                    LocalizationManager.getString("delivery.confirmComplete"),
                    ButtonType.YES, ButtonType.NO);
            conf.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    try {
                        reportDAO.completeDeliveryProcedure(sel.getId());

                        sel.setStatus(STATUS_COMPLETED);
                        tableDeliveries.refresh();
                        updateChangeStatusButton(sel);

                        updateStatus(LocalizationManager.getString("delivery.completed"));
                        showAlert(Alert.AlertType.INFORMATION, "Успех",
                                LocalizationManager.getString("delivery.completed"));
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Ошибка",
                                "Не удалось провести поставку: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else if (STATUS_COMPLETED.equals(currentStatus)) {
            showAlert(Alert.AlertType.WARNING, "Внимание",
                    "Нельзя отменить проведение поставки!\n" +
                            "Проведенные документы не подлежат отмене.");
        }
    }

    private void loadDeliveryDictionaries() {
        companyMap.clear(); for (Company c : companyDAO.getAllCompanies()) companyMap.put(c.getId(), c.getName());
        storeMap.clear(); for (Store s : storeDao.getAllStores()) storeMap.put(s.getId(), s.getName());
    }

    private void loadDeliveryItems(int id) {
        delItemsData.clear();
        delItemsData.addAll(itemDao.getItemsByDelivery(id));
        tableDeliveryItems.setItems(delItemsData);
        BigDecimal sum = BigDecimal.ZERO;
        for (DeliveryItem i : delItemsData) {
            if (i.getTotal() != null) sum = sum.add(i.getTotal());
        }
        lblDeliveryTotal.setText(sum.toString());
    }

    private void showDelItemDialog(Delivery d, DeliveryItem editItem) {
        Dialog<DeliveryItem> dialog = new Dialog<>();
        dialog.setTitle(editItem == null ? "Добавить товар" : "Редактировать товар");
        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(20));

        ComboBox<Product> cbProd = new ComboBox<>();
        ObservableList<Product> allProds = FXCollections.observableArrayList(productDAO.getAllProducts());
        cbProd.setItems(allProds);
        cbProd.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product p) {
                return p == null ? "" : p.getName() + " (" + p.getArticleNumber() + ")";
            }

            @Override
            public Product fromString(String s) {
                return null;
            }
        });

        cbProd.setEditable(true);

        final boolean[] isSettingPrice = {false};

        TextField tQty = new TextField("1");
        TextField tPrice = new TextField("0");

        TextField editor = cbProd.getEditor();

        editor.addEventFilter(javafx.scene.input.KeyEvent.KEY_RELEASED, event -> {
            String filter = editor.getText().toLowerCase().trim();

            if (filter.isEmpty()) {
                cbProd.setItems(allProds);
            } else {
                ObservableList<Product> filteredList = FXCollections.observableArrayList();
                for (Product p : allProds) {
                    if (p.getName().toLowerCase().contains(filter) ||
                            p.getArticleNumber().toLowerCase().contains(filter)) {
                        filteredList.add(p);
                    }
                }
                cbProd.setItems(filteredList);
            }

            if (!cbProd.isShowing()) {
                cbProd.show();
            }
        });

        cbProd.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cbProd.getSelectionModel().select(newVal);

                javafx.application.Platform.runLater(() -> {
                    editor.setText(newVal.getName() + " (" + newVal.getArticleNumber() + ")");
                    editor.positionCaret(editor.getText().length());
                });

                cbProd.hide();

                if (editItem == null && !isSettingPrice[0]) {
                    isSettingPrice[0] = true;
                    if (newVal.getPrice() != null) {
                        tPrice.setText(newVal.getPrice().toString());
                    } else {
                        tPrice.setText("0");
                    }
                    isSettingPrice[0] = false;
                }
            }
        });

        if (editItem != null) {
            cbProd.setDisable(true);
            Product selectedProduct = allProds.stream()
                    .filter(p -> p.getId() == editItem.getProductId())
                    .findFirst()
                    .orElse(null);
            cbProd.setValue(selectedProduct);
            if (selectedProduct != null) {
                editor.setText(selectedProduct.getName() + " (" + selectedProduct.getArticleNumber() + ")");
            }
            tQty.setText(String.valueOf(editItem.getQuantity()));
            tPrice.setText(editItem.getPrice().toString());
        }

        g.add(new Label("Товар (начните вводить для поиска):"), 0, 0);
        g.add(cbProd, 1, 0);
        g.add(new Label("Кол-во:"), 0, 1);
        g.add(tQty, 1, 1);
        g.add(new Label("Цена:"), 0, 2);
        g.add(tPrice, 1, 2);

        dialog.getDialogPane().setContent(g);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.setDisable(editItem == null);

        cbProd.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal == null);
        });

        dialog.setResultConverter(b -> {
            if (b == saveBtn) {
                Product p = cbProd.getValue();

                if (p == null && !editor.getText().isEmpty()) {
                    String editorText = editor.getText().trim();
                    p = allProds.stream()
                            .filter(prod -> (prod.getName() + " (" + prod.getArticleNumber() + ")")
                                    .equalsIgnoreCase(editorText))
                            .findFirst()
                            .orElse(null);
                }

                if (p != null) {
                    try {
                        DeliveryItem item = new DeliveryItem();
                        item.setDeliveryId(d.getId());
                        item.setProductId(p.getId());
                        item.setQuantity(Integer.parseInt(tQty.getText()));
                        item.setPrice(new BigDecimal(tPrice.getText().replace(",", ".")));
                        return item;
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Некорректные данные!\nПроверьте поля 'Кол-во' и 'Цена'");
                        alert.showAndWait();
                        return null;
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "Пожалуйста, выберите товар из списка!");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
            if (editItem != null) {
                if (itemDao.updateItem(item)) {
                    loadDeliveryItems(d.getId());
                }
            } else {
                if (itemDao.addItem(item)) {
                    loadDeliveryItems(d.getId());
                }
            }
        });
    }

    private void loadDelData() {
        delData.clear();
        delData.addAll(delDao.getAllDeliveries());
        tableDeliveries.setItems(delData);
    }

    private void updateStatus(String msg) {
        if (statusCallback != null) statusCallback.accept(msg);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}