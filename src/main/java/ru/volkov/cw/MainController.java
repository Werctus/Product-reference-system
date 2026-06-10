package ru.volkov.cw;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import ru.volkov.cw.model.DeliveryItem;
import ru.volkov.cw.dao.DeliveryItemDAO;
import javafx.event.ActionEvent;
import java.util.function.UnaryOperator;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.volkov.cw.dao.*;
import ru.volkov.cw.model.*;
import util.LocalizationManager;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import ru.volkov.cw.service.ReportService;
import ru.volkov.cw.service.InventoryService;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.math.BigDecimal;

public class MainController {
    @FXML private ComboBox<String> cbLanguage;
    @FXML private TabPane mainTabPane;
    @FXML private Label lblStatusBar;

    @FXML private Button btnAddToStore;
    @FXML private Button btnUpdateQuantity;

    @FXML private TableView<Company> tableCompanies;
    @FXML private TableColumn<Company, String> colCompanyName;
    @FXML private TableColumn<Company, String> colCompanyINN;
    @FXML private TableColumn<Company, BigDecimal> colCompanyRating;
    @FXML private TableColumn<Company, String> colCompanyNumber;
    @FXML private TableColumn<Company, String> colCompanyAddress;

    @FXML private Button btnAddCompany;
    @FXML private Button btnEditCompany;
    @FXML private Button btnDeleteCompany;

    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, String> colProductArticle;
    @FXML private TableColumn<Product, BigDecimal> colProductPrice;
    @FXML private TableColumn<Product, Integer> colProductBrand;
    @FXML private TableColumn<Product, Integer> colProductUnit;

    @FXML private TitledPane productFormPane;
    @FXML private TextField txtProductName;
    @FXML private TextField txtProductCharacteristic;
    @FXML private TextField txtProductPrice;
    @FXML private TextField txtProductDiscount;
    @FXML private TextField txtProductVat;
    @FXML private ComboBox<Brand> cbProductBrand;
    @FXML private ComboBox<UnitOfMeasure> cbProductUnit;

    @FXML private ComboBox<Store> cbStoreSelect;
    @FXML private TableView<InventoryItem> tableStoreInventory;
    @FXML private TableColumn<InventoryItem, String> colInventoryProduct;
    @FXML private TableColumn<InventoryItem, Integer> colInventoryQuantity;
    @FXML private TableColumn<InventoryItem, Integer> colInventoryMinLimit;
    @FXML private TableColumn<InventoryItem, String> colInventorySpecs;

    @FXML private Button btnAddProduct;
    @FXML private Button btnEditProduct;
    @FXML private Button btnDeleteProduct;
    @FXML private Button btnRefreshProduct;
    @FXML private Button btnSaveProduct;
    @FXML private Button btnCancelProduct;

    @FXML private ImageView imgProduct;
    @FXML private Button btnChooseImg;

    @FXML private Button btnFastAddBrand;
    @FXML private Button btnFastAddUnit;
    @FXML private Label lblBrandSuggestion;
    @FXML private Label lblUnitSuggestion;

    @FXML private ListView<Brand> listBrands;
    @FXML private TextField txtBrandName;
    @FXML private Button btnAddBrand;

    @FXML private ListView<UnitOfMeasure> listUnits;
    @FXML private TextField txtUnitName;
    @FXML private Button btnAddUnit;

    @FXML private ComboBox<Category> cbMainCat;
    @FXML private ComboBox<Category> cbSubCat;
    @FXML private Button btnFastMainCat;
    @FXML private Button btnFastSubCat;
    @FXML private Label lblMainCatSug;
    @FXML private Label lblSubCatSug;

    @FXML private TreeView<Category> treeCategories;
    @FXML private TextField txtCategoryName;
    @FXML private ComboBox<Category> cbParentCategory;
    @FXML private Button btnDeleteCategory;
    @FXML private Button btnSaveCategory;

    @FXML private TableView<Store> tableStores;
    @FXML private TableColumn<Store, String> colStoreName;
    @FXML private TableColumn<Store, String> colStorePhone;
    @FXML private TableColumn<Store, String> colStoreAddress;
    @FXML private TableColumn<Store, String> colStoreEmail;

    @FXML private Button btnAddStore;
    @FXML private Button btnEditStore;
    @FXML private Button btnDeleteStore;

    @FXML private TableColumn<Delivery, String> colDelNum;
    @FXML private TableColumn<Delivery, String> colDelStatus;
    @FXML private TableColumn<Delivery, Integer> colDelComp;
    @FXML private TableColumn<Delivery, Integer> colDelStore;
    @FXML private TableColumn<Delivery, LocalDateTime> colDelDate;

    @FXML private TableView<Delivery> tableDeliveries;
    @FXML private Button btnAddDelivery;
    @FXML private Button btnEditDelivery;
    @FXML private Button btnDeleteDelivery;

    @FXML private TableView<DeliveryItem> tableDeliveryItems;
    @FXML private TableColumn<DeliveryItem, String> colDeliveryItemProduct;
    @FXML private TableColumn<DeliveryItem, Integer> colDeliveryItemQuantity;
    @FXML private TableColumn<DeliveryItem, BigDecimal> colDeliveryItemPrice;
    @FXML private TableColumn<DeliveryItem, BigDecimal> colDeliveryItemTotal;

    @FXML private Label lblDeliveryTotal;

    // Новые кнопки для управления составом поставки
    @FXML private Button btnAddDeliveryItem;
    @FXML private Button btnEditDeliveryItem;
    @FXML private Button btnDeleteDeliveryItem;

    @FXML private TextField txtSearchProduct;
    @FXML private ComboBox<Category> cbFilterCategory;

    @FXML private ResourceBundle resources;

    @FXML private ComboBox<Product> cbPriceHistoryProduct;
    @FXML private DatePicker dpPriceHistoryFrom;
    @FXML private DatePicker dpPriceHistoryTo;
    @FXML private Button btnPriceHistoryFilter;
    @FXML private Button btnPriceHistoryReset;
    @FXML private TableView<PriceHistory> tablePriceHistory;
    @FXML private TableColumn<PriceHistory, String> colPriceProduct;
    @FXML private TableColumn<PriceHistory, BigDecimal> colPriceOld;
    @FXML private TableColumn<PriceHistory, BigDecimal> colPriceNew;
    @FXML private TableColumn<PriceHistory, LocalDateTime> colPriceDate;

    @FXML private Button btnDeleteBrand;
    @FXML private Button btnDeleteUnit;

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label totalStoresLabel;

    private InventoryService inventoryService;
    private ReportService reportService;

    /**
     * Устанавливает сервис инвентаря.
     *
     * @param inventoryService сервис для получения данных об инвентаре
     */
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        loadInventoryStatus();
    }

    /**
     * Устанавливает сервис отчётов.
     *
     * @param reportService сервис для генерации отчётов
     */
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Загружает и отображает статус инвентаря в карточках.
     */
    private void loadInventoryStatus() {
        if (inventoryService == null) {
            return;
        }
        try {
            totalProductsLabel.setText(String.valueOf(inventoryService.getTotalProductsCount()));
            lowStockLabel.setText(String.valueOf(inventoryService.getLowStockProductsCount()));
            totalStoresLabel.setText(String.valueOf(inventoryService.getTotalStoresCount()));
        } catch (Exception e) {
            showError("Ошибка загрузки данных", "Не удалось получить статус инвентаря: " + e.getMessage());
        }
    }

    /**
     * Обработчик кнопки «Отчёт по поставкам».
     */
    @FXML
    private void handleSuppliesReport() {
        if (reportService != null) {
            reportService.generateSuppliesReport();
        }
    }

    /**
     * Обработчик кнопки «Отчёт по изменению цен».
     */
    @FXML
    private void handlePriceChangesReport() {
        if (reportService != null) {
            reportService.generatePriceChangesReport();
        }
    }

    /**
     * Обработчик кнопки «Оценка инвентаря».
     */
    @FXML
    private void handleInventoryValuation() {
        if (reportService != null) {
            reportService.generateInventoryValuation();
        }
    }

    /**
     * Показывает диалоговое окно с ошибкой.
     *
     * @param title   заголовок окна
     * @param message текст ошибки
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private byte[] imgBytes = null;
    private Category currentEditingCategory = null;
    private FilteredList<Product> filteredProducts;
    private final CompanyDAO companyDAO = new CompanyDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final UnitOfMeasureDAO unitDAO = new UnitOfMeasureDAO();
    private final CategoryDAO catDao = new CategoryDAO();
    private final StoreDAO storeDao = new StoreDAO();
    private final DeliveryDAO delDao = new DeliveryDAO();
    private final DeliveryItemDAO itemDao = new DeliveryItemDAO();
    private final PriceHistoryDAO priceHistoryDAO = new PriceHistoryDAO();
    private ObservableList<PriceHistory> priceHistoryData = FXCollections.observableArrayList();

    private ObservableList<Store> storeData = FXCollections.observableArrayList();
    private ObservableList<Delivery> delData = FXCollections.observableArrayList();
    private ObservableList<DeliveryItem> delItemsData = FXCollections.observableArrayList();
    private ObservableList<Product> productData = FXCollections.observableArrayList();
    private ObservableList<Company> companyData = FXCollections.observableArrayList();

    private Map<Integer, String> brandMap = new HashMap<>();
    private Map<Integer, String> unitMap = new HashMap<>();

    private Map<Integer, String> companyMap = new HashMap<>();
    private Map<Integer, String> storeMap = new HashMap<>();

    private Product currentEditingProduct = null;

    private final InventoryDAO invDao = new InventoryDAO();
    private ObservableList<InventoryItem> invData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initLanguageSwitcher();
        initStatusBar();

        initCompanyTab();
        initProductTab();
        initReferencesTab();
        initCategoryTab();
        initStoreTab();
        initDeliveryTab();
        initProductFilters();
        initInventoryTab();
        initPriceHistoryTab();
        loadInventoryStatus();
    }

    private void initInventoryTab() {
        // Кнопка: Добавить товар в магазин
        colInventoryProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colInventoryQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colInventoryMinLimit.setCellValueFactory(new PropertyValueFactory<>("minStock"));

        // Если specs это JSONB, лучше использовать кастомный factory
        colInventorySpecs.setCellValueFactory(cellData -> {
            InventoryItem item = cellData.getValue();
            String specsText = "";
            return new SimpleStringProperty(specsText);
        });

        btnAddToStore.setOnAction(e -> {
            Store s = cbStoreSelect.getSelectionModel().getSelectedItem();
            if (s == null) { showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите магазин!"); return; }
            showInventoryDialog(s, null);
        });

// Кнопка: Обновить количество/лимиты
        btnUpdateQuantity.setOnAction(e -> {
            Store s = cbStoreSelect.getSelectionModel().getSelectedItem();
            InventoryItem item = tableStoreInventory.getSelectionModel().getSelectedItem();
            if (s == null || item == null) { showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите товар в таблице!"); return; }
            showInventoryDialog(s, item);
        });
    }

    private void initProductFilters() {
        // 1. Настройка ComboBox категорий (фильтр)
        cbFilterCategory.getItems().add(null); // Вариант "Все категории"
        cbFilterCategory.getItems().addAll(catDao.getAllCategories());
        cbFilterCategory.setConverter(new StringConverter<Category>() {
            @Override public String toString(Category c) { return c == null ? "Все категории" : c.getName(); }
            @Override public Category fromString(String s) { return null; }
        });

        // 2. Инициализация FilteredList
        // Предположим, у тебя есть список всех товаров productData (ObservableList)
        filteredProducts = new FilteredList<>(productData, p -> true);
        tableProducts.setItems(filteredProducts);

        // 3. Логика фильтрации (события для поиска и выбора категории)
        // Этот метод будет вызываться при изменении любого из фильтров
        Runnable applyFilter = () -> {
            String searchText = txtSearchProduct.getText().toLowerCase();
            Category selectedCat = cbFilterCategory.getValue();

            filteredProducts.setPredicate(p -> {
                // Фильтр по тексту
                boolean matchName = searchText.isEmpty() ||
                        p.getName().toLowerCase().contains(searchText);

                // Фильтр по категории
                boolean matchCategory = (selectedCat == null) ||
                        (p.getCategoryId() == selectedCat.getId());

                return matchName && matchCategory;
            });
        };

        // Привязываем слушатели
        txtSearchProduct.textProperty().addListener(obs -> applyFilter.run());
        cbFilterCategory.valueProperty().addListener(obs -> applyFilter.run());
    }

    // Вызов диалога для добавления или изменения
    private void showInventoryDialog(Store s, InventoryItem item) {
        Dialog<Boolean> d = new Dialog<>();
        d.setTitle(item == null ? "Добавить товар" : "Обновить остатки");

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        g.setPadding(new Insets(20));

        ComboBox<Product> cbProd = new ComboBox<>();
        ObservableList<Product> allProducts = FXCollections.observableArrayList(productDAO.getAllProducts());
        cbProd.setItems(allProducts);

        // === Настраиваем отображение продукта ===
        cbProd.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product p) {
                return p == null ? "" : p.getName() + " (" + p.getArticleNumber() + ")";
            }

            @Override
            public Product fromString(String str) {
                if (str == null || str.trim().isEmpty()) return null;
                // Ищем продукт по полному тексту (имя + артикул)
                return allProducts.stream()
                        .filter(p -> (p.getName() + " (" + p.getArticleNumber() + ")").equalsIgnoreCase(str.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });

        // === Делаем ComboBox редактируемым с фильтрацией ===
        cbProd.setEditable(true);

        // Сохраняем ссылку на отфильтрованный список
        FilteredList<Product> filteredProducts = new FilteredList<>(allProducts, p -> true);
        cbProd.setItems(filteredProducts);

        // Слушатель для фильтрации при вводе
        cbProd.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                filteredProducts.setPredicate(p -> true); // Показываем все
            } else {
                String filter = newText.toLowerCase();
                filteredProducts.setPredicate(p ->
                        p.getName().toLowerCase().contains(filter) ||
                                p.getArticleNumber().toLowerCase().contains(filter)
                );
            }
            // Открываем выпадающий список при вводе
            if (!cbProd.isShowing()) {
                cbProd.show();
            }
        });

        // При выборе элемента - закрываем список
        cbProd.setOnHidden(e -> {
            Product selected = cbProd.getValue();
            if (selected != null) {
                cbProd.getEditor().setText(cbProd.getConverter().toString(selected));
            }
        });

        TextField txtQty = new TextField();
        txtQty.setPromptText("Текущее кол-во");

        TextField txtMin = new TextField();
        txtMin.setPromptText("Мин. запас (лимит)");

        // Если редактируем - блокируем смену товара
        if (item != null) {
            cbProd.setDisable(true);
            cbProd.setEditable(false);
            txtQty.setText(String.valueOf(item.getQuantity()));
            txtMin.setText(String.valueOf(item.getMinStock()));
        }

        g.addRow(0, new Label("Товар:"), cbProd);
        g.addRow(1, new Label("Кол-во:"), txtQty);
        g.addRow(2, new Label("Мин. запас:"), txtMin);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    int qty = Integer.parseInt(txtQty.getText().trim());
                    int min = Integer.parseInt(txtMin.getText().trim());
                    if (item == null) {
                        return invDao.addProductToStore(s.getId(), cbProd.getValue().getId(), qty, min);
                    } else {
                        return invDao.updateInventory(s.getId(), item.getProductId(), qty, min);
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Проверьте правильность ввода чисел!");
                    return false;
                }
            }
            return false;
        });

        d.showAndWait().ifPresent(res -> {
            if (res) loadInventoryForStore(s.getId());
        });
    }

    private void initDeliveryTab() {
        loadDeliveryDictionaries();

        colDelNum.setCellValueFactory(new PropertyValueFactory<>("documentNumber"));
        colDelStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colDelStatus.setCellFactory(col -> new TableCell<Delivery, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                } else {
                    String translationKey = "status." + status.toLowerCase();
                    try {
                        setText(resources.getString(translationKey));
                    } catch (Exception e) {
                        setText(status);
                    }
                }
            }
        });

        colDelDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colDelDate.setCellFactory(col -> new TableCell<Delivery, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setText(null);
                } else {
                    setText(d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }
            }
        });

        colDelComp.setCellValueFactory(new PropertyValueFactory<>("companyId"));
        colDelComp.setCellFactory(col -> new TableCell<Delivery, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null || id == 0) setText(null);
                else setText(companyMap.getOrDefault(id, "Неизвестно"));
            }
        });

        colDelStore.setCellValueFactory(new PropertyValueFactory<>("storeId"));
        colDelStore.setCellFactory(col -> new TableCell<Delivery, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null || id == 0) setText(null);
                else setText(storeMap.getOrDefault(id, "Неизвестно"));
            }
        });

        colDeliveryItemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colDeliveryItemQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDeliveryItemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDeliveryItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tableDeliveries.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadDeliveryItems(newVal.getId());
                btnAddDeliveryItem.setDisable(false);
            } else {
                delItemsData.clear();
                tableDeliveryItems.setItems(delItemsData);
                lblDeliveryTotal.setText("0.00");
                btnAddDeliveryItem.setDisable(true);
            }
            // Сбрасываем кнопки редактирования/удаления товара при смене поставки
            btnEditDeliveryItem.setDisable(true);
            btnDeleteDeliveryItem.setDisable(true);
        });

        // Слушатель для нижней таблицы, чтобы активировать кнопки Изменить/Удалить
        tableDeliveryItems.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSelected = (newVal != null);
            if(btnEditDeliveryItem != null) btnEditDeliveryItem.setDisable(!isSelected);
            if(btnDeleteDeliveryItem != null) btnDeleteDeliveryItem.setDisable(!isSelected);
        });

        btnAddDeliveryItem.setOnAction(e -> {
            Delivery d = tableDeliveries.getSelectionModel().getSelectedItem();
            if (d != null) showDelItemDialog(d, null);
        });

        if (btnEditDeliveryItem != null) {
            btnEditDeliveryItem.setOnAction(e -> {
                Delivery d = tableDeliveries.getSelectionModel().getSelectedItem();
                DeliveryItem item = tableDeliveryItems.getSelectionModel().getSelectedItem();
                if (d != null && item != null) showDelItemDialog(d, item);
            });
        }

        if (btnDeleteDeliveryItem != null) {
            btnDeleteDeliveryItem.setOnAction(e -> {
                Delivery d = tableDeliveries.getSelectionModel().getSelectedItem();
                DeliveryItem item = tableDeliveryItems.getSelectionModel().getSelectedItem();

                if (d != null && item != null) {
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить товар из поставки?", ButtonType.YES, ButtonType.NO);
                    conf.showAndWait().ifPresent(resp -> {
                        if (resp == ButtonType.YES) {
                            if (itemDao.deleteItem(d.getId(), item.getProductId())) {
                                loadDeliveryItems(d.getId());
                                lblStatusBar.setText("Товар удален из поставки");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить товар");
                            }
                        }
                    });
                }
            });
        }

        setupDeliveryButtons();
        loadDelData();
    }

    private void loadDeliveryDictionaries() {
        companyMap.clear();
        for (Company c : companyDAO.getAllCompanies()) {
            companyMap.put(c.getId(), c.getName());
        }
        storeMap.clear();
        for (Store s : storeDao.getAllStores()) {
            storeMap.put(s.getId(), s.getName());
        }
    }

    private void loadDeliveryItems(int deliveryId) {
        delItemsData.clear();
        delItemsData.addAll(itemDao.getItemsByDelivery(deliveryId));
        tableDeliveryItems.setItems(delItemsData);

        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < delItemsData.size(); i++) {
            if (delItemsData.get(i).getTotal() != null) {
                sum = sum.add(delItemsData.get(i).getTotal());
            }
        }
        lblDeliveryTotal.setText(sum.toString());
    }

    // Универсальный диалог для Добавления и Изменения
    private void showDelItemDialog(Delivery d, DeliveryItem editItem) {
        boolean isEditMode = (editItem != null);

        Dialog<DeliveryItem> dialog = new Dialog<>();
        dialog.setTitle(isEditMode ? "Изменить товар" : "Добавить товар");
        dialog.setHeaderText((isEditMode ? "Изменение" : "Добавление") + " товара в поставке №" + d.getDocumentNumber());

        ButtonType btnSave = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        g.setPadding(new Insets(20, 50, 10, 10));

        ObservableList<Product> allProducts = FXCollections.observableArrayList(productDAO.getAllProducts());

        ComboBox<Product> cbProd = new ComboBox<>();
        cbProd.setEditable(true);
        cbProd.setItems(allProducts);

        cbProd.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product p) {
                return p == null ? "" : p.getName();
            }
            @Override
            public Product fromString(String str) {
                if (str == null || str.trim().isEmpty()) return null;
                return allProducts.stream()
                        .filter(p -> p.getName().equalsIgnoreCase(str.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });

        cbProd.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            Product selected = cbProd.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getName().equals(newText)) return;

            if (newText == null || newText.trim().isEmpty()) {
                cbProd.setItems(allProducts);
            } else {
                String lowerCaseFilter = newText.toLowerCase();
                ObservableList<Product> filtered = allProducts.filtered(p ->
                        p.getName().toLowerCase().contains(lowerCaseFilter)
                );
                cbProd.setItems(filtered);
                cbProd.show();
            }
        });

        TextField tQty = new TextField("1");
        TextField tPrice = new TextField("0.00");

        cbProd.setOnAction(ev -> {
            Product p = cbProd.getSelectionModel().getSelectedItem();
            if (p != null && p.getPrice() != null && !isEditMode) {
                tPrice.setText(p.getPrice().toString());
            }
        });

        // Если режим редактирования, заполняем поля и блокируем выбор другого товара
        if (isEditMode) {
            Product currentProd = allProducts.stream()
                    .filter(p -> p.getId() == editItem.getProductId())
                    .findFirst().orElse(null);

            cbProd.setValue(currentProd);
            cbProd.setDisable(true); // Товар менять нельзя, только кол-во и цену

            tQty.setText(String.valueOf(editItem.getQuantity()));
            tPrice.setText(editItem.getPrice().toString());
        }

        g.add(new Label("Товар:"), 0, 0); g.add(cbProd, 1, 0);
        g.add(new Label("Количество:"), 0, 1); g.add(tQty, 1, 1);
        g.add(new Label("Цена закуп.:"), 0, 2); g.add(tPrice, 1, 2);

        dialog.getDialogPane().setContent(g);

        dialog.setResultConverter(b -> {
            if (b == btnSave) {
                Product p = cbProd.getValue();
                if (p != null) {
                    try {
                        int q = Integer.parseInt(tQty.getText().trim());
                        BigDecimal c = new BigDecimal(tPrice.getText().trim().replace(",", "."));

                        DeliveryItem item = new DeliveryItem();
                        item.setDeliveryId(d.getId());
                        item.setProductId(p.getId());
                        item.setQuantity(q);
                        item.setPrice(c);
                        return item;
                    } catch (Exception ex) {
                        lblStatusBar.setText("Ошибка: проверьте правильность ввода чисел");
                    }
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
            boolean success = isEditMode ? itemDao.updateItem(item) : itemDao.addItem(item);

            if (success) {
                loadDeliveryItems(d.getId());
                lblStatusBar.setText(isEditMode ? "Товар обновлен" : "Товар добавлен в поставку");
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось сохранить изменения в БД.");
            }
        });
    }

    private void setupDeliveryButtons() {
        btnAddDelivery.setOnAction(e -> {
            Dialog<Delivery> d = new Dialog<>();
            d.setTitle("Новая поставка");
            d.setHeaderText("Создание документа поставки");

            ButtonType btnSave = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
            d.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

            GridPane g = new GridPane();
            g.setHgap(10); g.setVgap(10);
            g.setPadding(new Insets(20, 50, 10, 10));

            ComboBox<Company> cbComp = new ComboBox<>();
            cbComp.setConverter(new StringConverter<Company>() {
                @Override public String toString(Company c) { return c == null ? "" : c.getName(); }
                @Override public Company fromString(String s) { return null; }
            });
            cbComp.getItems().addAll(companyDAO.getAllCompanies());

            ComboBox<Store> cbStore = new ComboBox<>();
            cbStore.setConverter(new StringConverter<Store>() {
                @Override public String toString(Store s) { return s == null ? "" : s.getName(); }
                @Override public Store fromString(String s) { return null; }
            });
            cbStore.getItems().addAll(storeDao.getAllStores());

            g.add(new Label("Фирма:"), 0, 0);   g.add(cbComp, 1, 0);
            g.add(new Label("Магазин:"), 0, 1); g.add(cbStore, 1, 1);

            d.getDialogPane().setContent(g);

            Button okBtn = (Button) d.getDialogPane().lookupButton(btnSave);
            okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
                boolean isValid = true;
                String errorStyle = "-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 3px;";
                String defaultStyle = "";

                if (cbComp.getValue() == null) { cbComp.setStyle(errorStyle); isValid = false; }
                else { cbComp.setStyle(defaultStyle); }

                if (cbStore.getValue() == null) { cbStore.setStyle(errorStyle); isValid = false; }
                else { cbStore.setStyle(defaultStyle); }

                if (!isValid) {
                    ev.consume();
                    lblStatusBar.setText("ОШИБКА: Заполните все поля!");
                }
            });

            d.setResultConverter(b -> {
                if (b == btnSave) {
                    return new Delivery("", cbComp.getValue().getId(), cbStore.getValue().getId());
                }
                return null;
            });

            d.showAndWait().ifPresent(del -> {
                int newId = delDao.addDelivery(del);
                if (newId != -1) {
                    loadDelData();
                    lblStatusBar.setText("Поставка успешно создана!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось создать поставку в БД");
                }
            });
        });

        btnEditDelivery.setOnAction(e -> {
            Delivery sel = tableDeliveries.getSelectionModel().getSelectedItem();
            if (sel != null) {
                showAlert(Alert.AlertType.INFORMATION, "Инфо", "Функция редактирования поставки находится в разработке.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите поставку для редактирования!");
            }
        });

        btnDeleteDelivery.setOnAction(e -> {
            Delivery sel = tableDeliveries.getSelectionModel().getSelectedItem();
            if (sel != null) {
                if ("Проведен".equals(sel.getStatus())) {
                    showAlert(Alert.AlertType.WARNING, "Отказ", "Нельзя удалить проведенную поставку!");
                    return;
                }

                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить документ?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.YES) {
                        if (delDao.deleteDelivery(sel.getId())) {
                            delData.remove(sel);
                            lblStatusBar.setText("Поставка удалена.");
                        }
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите поставку!");
            }
        });
    }

    private void loadDelData() {
        delData.clear();
        delData.addAll(delDao.getAllDeliveries());
        tableDeliveries.setItems(delData);
    }

    private void initLanguageSwitcher() {
        if (cbLanguage != null) {
            cbLanguage.getItems().addAll(
                    "RU Русский",
                    "EN English",
                    "PL Polski"
            );

            Locale current = LocalizationManager.getCurrentLocale();
            if ("pl".equals(current.getLanguage())) {
                cbLanguage.getSelectionModel().select(2);
            } else if ("en".equals(current.getLanguage())) {
                cbLanguage.getSelectionModel().select(1);
            } else {
                cbLanguage.getSelectionModel().select(0);
            }

            cbLanguage.setOnAction(e -> handleChangeLanguage());
        }
    }

    private void initStoreTab() {
        colStoreName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStorePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colStoreAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStoreEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // === НАСТРОЙКА ComboBox для выбора магазина ===
        cbStoreSelect.setConverter(new StringConverter<Store>() {
            @Override
            public String toString(Store store) {
                return store == null ? "" : store.getName(); // Показываем имя магазина
            }

            @Override
            public Store fromString(String string) {
                return null; // Не используется, так как пользователь выбирает из списка
            }
        });

        tableStores.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cbStoreSelect.getSelectionModel().select(newVal);
                // Автоматически загружаем инвентарь для выбранного магазина
                loadInventoryForStore(newVal.getId());
            }
        });

        setupStoreButtons();
        loadStoresData();
    }

    private void loadInventoryForStore(int storeId) {
        invData.clear();
        invData.addAll(invDao.getItems(storeId));
        tableStoreInventory.setItems(invData);
    }

    private void loadStoresData() {
        storeData.clear();
        storeData.addAll(storeDao.getAllStores());
        tableStores.setItems(storeData);
    }

    private Optional<Store> showStoreDialog(Store editStore) {
        Dialog<Store> d = new Dialog<>();
        d.setTitle(editStore == null ? "Добавить магазин" : "Редактировать магазин");
        d.setHeaderText("Заполните данные магазина");

        ButtonType btnSave = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        g.setPadding(new Insets(20, 150, 10, 10));

        TextField txtName = new TextField(); txtName.setPromptText("Название");
        TextField txtAddr = new TextField(); txtAddr.setPromptText("Адрес");

        TextField txtPhone = new TextField(); txtPhone.setPromptText("9991234567");
        UnaryOperator<TextFormatter.Change> filter = ch -> {
            if (ch.getControlNewText().matches("\\d{0,10}")) return ch;
            return null;
        };
        txtPhone.setTextFormatter(new TextFormatter<>(filter));

        HBox boxPhone = new HBox(5, new Label("+7"), txtPhone);
        boxPhone.setAlignment(Pos.CENTER_LEFT);

        TextField txtEmail = new TextField(); txtEmail.setPromptText("example@mail.ru");

        if (editStore != null) {
            txtName.setText(editStore.getName());
            txtAddr.setText(editStore.getAddress());
            String p = editStore.getPhone();
            if (p != null && p.startsWith("+7")) txtPhone.setText(p.substring(2));
            else txtPhone.setText(p);
        }

        g.add(new Label("Название:"), 0, 0); g.add(txtName, 1, 0);
        g.add(new Label("Телефон:"), 0, 1);  g.add(boxPhone, 1, 1);
        g.add(new Label("Адрес:"), 0, 2);    g.add(txtAddr, 1, 2);
        g.add(new Label("Email:"), 0, 3);    g.add(txtEmail, 1, 3);

        d.getDialogPane().setContent(g);

        Button btnOk = (Button) d.getDialogPane().lookupButton(btnSave);
        btnOk.addEventFilter(ActionEvent.ACTION, ev -> {
            boolean ok = true;
            String err = "-fx-border-color: red; -fx-border-width: 2px;";
            String def = "";

            if (txtName.getText().trim().isEmpty()) { txtName.setStyle(err); ok = false; } else txtName.setStyle(def);
            if (txtAddr.getText().trim().isEmpty()) { txtAddr.setStyle(err); ok = false; } else txtAddr.setStyle(def);
            if (txtPhone.getText().length() != 10) { txtPhone.setStyle(err); ok = false; } else txtPhone.setStyle(def);

            String emailText = txtEmail.getText().trim();
            if (!emailText.isEmpty()) {
                if (!emailText.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    txtEmail.setStyle(err); ok = false;
                } else txtEmail.setStyle(def);
            } else txtEmail.setStyle(def);

            if (!ok) {
                ev.consume();
                lblStatusBar.setText("ОШИБКА: Проверьте правильность заполнения полей!");
            }
        });

        d.setResultConverter(b -> {
            if (b == btnSave) {
                String fullP = "+7" + txtPhone.getText();
                String email = txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim();

                if (editStore == null) return new Store(0, txtName.getText(), fullP, txtAddr.getText(), email);
                else {
                    editStore.setName(txtName.getText());
                    editStore.setPhone(fullP);
                    editStore.setAddress(txtAddr.getText());
                    editStore.setEmail(email);
                    return editStore;
                }
            }
            return null;
        });

        return d.showAndWait();
    }

    private void initPriceHistoryTab() {
        // Инициализация колонок
        colPriceProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPriceOld.setCellValueFactory(new PropertyValueFactory<>("oldPrice"));
        colPriceNew.setCellValueFactory(new PropertyValueFactory<>("newPrice"));
        colPriceDate.setCellValueFactory(new PropertyValueFactory<>("changedAt"));

        // Форматирование даты
        colPriceDate.setCellFactory(col -> new TableCell<PriceHistory, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }
            }
        });

        // Заполнение ComboBox продуктами
        cbPriceHistoryProduct.getItems().add(null); // Для отображения "Все товары"
        cbPriceHistoryProduct.getItems().addAll(productDAO.getAllProducts());
        cbPriceHistoryProduct.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product == null ? "Все товары" : product.getName();
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });

        // Кнопка Фильтр
        btnPriceHistoryFilter.setOnAction(e -> filterPriceHistory());

        // Кнопка Сбросить
        btnPriceHistoryReset.setOnAction(e -> {
            cbPriceHistoryProduct.getSelectionModel().select(0);
            dpPriceHistoryFrom.setValue(null);
            dpPriceHistoryTo.setValue(null);
            loadPriceHistory();
        });

        // Загрузка данных
        loadPriceHistory();
    }

    private void loadPriceHistory() {
        priceHistoryData.clear();
        priceHistoryData.addAll(priceHistoryDAO.getPriceHistory());
        tablePriceHistory.setItems(priceHistoryData);
    }

    private void filterPriceHistory() {
        Product selectedProduct = cbPriceHistoryProduct.getValue();
        LocalDate fromDate = dpPriceHistoryFrom.getValue();
        LocalDate toDate = dpPriceHistoryTo.getValue();

        priceHistoryData.clear();

        if (selectedProduct != null) {
            // Фильтр по продукту
            ObservableList<PriceHistory> filtered;
            if (fromDate != null && toDate != null) {
                filtered = priceHistoryDAO.getPriceHistoryByDateRange(
                        fromDate.atStartOfDay(),
                        toDate.atTime(23, 59, 59)
                ).filtered(ph -> ph.getProductId() == selectedProduct.getId());
            } else {
                filtered = priceHistoryDAO.getPriceHistoryByProduct(selectedProduct.getId());
            }
            priceHistoryData.addAll(filtered);
        } else if (fromDate != null && toDate != null) {
            // Фильтр только по дате
            priceHistoryData.addAll(priceHistoryDAO.getPriceHistoryByDateRange(
                    fromDate.atStartOfDay(),
                    toDate.atTime(23, 59, 59)
            ));
        } else {
            // Без фильтров
            priceHistoryData.addAll(priceHistoryDAO.getPriceHistory());
        }

        tablePriceHistory.setItems(priceHistoryData);
    }

    // Метод для логирования изменения цены (вызывайте при обновлении товара)
    private void logPriceChange(Product product, BigDecimal oldPrice) {
        if (product != null && oldPrice != null &&
                oldPrice.compareTo(product.getPrice()) != 0) {
            priceHistoryDAO.logPriceChange(
                    product.getId(),
                    oldPrice,
                    product.getPrice()
            );
        }
    }

    private void setupStoreButtons() {
        btnAddStore.setOnAction(e -> {
            Optional<Store> res = showStoreDialog(null);
            res.ifPresent(s -> {
                if (storeDao.addStore(s)) {
                    loadStoresData();
                    lblStatusBar.setText("Магазин добавлен: " + s.getName());
                } else showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось сохранить магазин в базу.");
            });
        });

        btnEditStore.setOnAction(e -> {
            Store sel = tableStores.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Optional<Store> res = showStoreDialog(sel);
                res.ifPresent(s -> {
                    if (storeDao.updateStore(s)) {
                        tableStores.refresh();
                        lblStatusBar.setText("Данные магазина обновлены.");
                    } else showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обновить данные.");
                });
            } else showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите магазин из списка!");
        });

        btnDeleteStore.setOnAction(e -> {
            Store sel = tableStores.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить магазин " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.YES) {
                        if (storeDao.deleteStore(sel.getId())) {
                            storeData.remove(sel);
                            lblStatusBar.setText("Магазин удален.");
                        } else showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить магазин.");
                    }
                });
            } else showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите магазин для удаления!");
        });
    }

    private void initCategoryTab() {
        treeCategories.setCellFactory(tv -> new TreeCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item.getName());
                    if (item.getParentId() == null || item.getParentId() == 0) {
                        setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-padding: 5px 0px;");
                    } else {
                        setStyle("-fx-font-weight: normal; -fx-font-size: 13px; -fx-text-fill: #34495e; -fx-padding: 2px 0px;");
                    }
                }
            }
        });

        cbParentCategory.setConverter(new StringConverter<Category>() {
            @Override public String toString(Category c) { return c == null ? "" : c.getName(); }
            @Override public Category fromString(String s) { return null; }
        });

        treeCategories.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null && newVal.getValue().getId() > 0) {
                fillCategoryForm(newVal.getValue());
                currentEditingCategory = newVal.getValue();
            }
        });

        setupCategoryButtons();
        refreshCategoryData();
    }

    private void refreshCategoryData() {
        loadCategoriesTree();
        loadParentCategoryCombo();
    }

    private void loadCategoriesTree() {
        Category rootCat = new Category();
        rootCat.setId(0); rootCat.setName("Корень");
        TreeItem<Category> rootItem = new TreeItem<>(rootCat);
        rootItem.setExpanded(true);

        List<Category> allCats = catDao.getAllCategories();

        for (Category c : allCats) {
            if (c.getParentId() == null || c.getParentId() == 0) {
                TreeItem<Category> mainItem = new TreeItem<>(c);
                mainItem.setExpanded(true);

                for (Category sub : allCats) {
                    if (sub.getParentId() != null && sub.getParentId().equals(c.getId())) {
                        mainItem.getChildren().add(new TreeItem<>(sub));
                    }
                }
                rootItem.getChildren().add(mainItem);
            }
        }

        treeCategories.setRoot(rootItem);
        treeCategories.setShowRoot(false);
    }

    private void setupCategoryButtons() {
        btnDeleteCategory.setOnAction(e -> {
            TreeItem<Category> selectedItem = treeCategories.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue().getId() > 0) {
                Category c = selectedItem.getValue();
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить категорию '" + c.getName() + "'?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        if (catDao.deleteCategory(c.getId())) {
                            refreshCategoryData();
                            txtCategoryName.clear();
                            lblStatusBar.setText("Категория удалена");
                            loadMainCats();
                        } else showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить. Возможно, к ней привязаны товары.");
                    }
                });
            } else showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите категорию для удаления!");
        });

        btnSaveCategory.setOnAction(e -> saveCategoryAction());
    }

    private void fillCategoryForm(Category c) {
        txtCategoryName.setText(c.getName());
        if (c.getParentId() == null || c.getParentId() == 0) {
            cbParentCategory.getSelectionModel().select(0);
        } else {
            for (Category parent : cbParentCategory.getItems()) {
                if (parent.getId() == c.getParentId()) {
                    cbParentCategory.getSelectionModel().select(parent);
                    break;
                }
            }
        }
    }

    private void saveCategoryAction() {
        String name = txtCategoryName.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Название категории не может быть пустым!");
            return;
        }

        Category parent = cbParentCategory.getSelectionModel().getSelectedItem();
        Integer parentId = (parent != null && parent.getId() > 0) ? parent.getId() : null;

        if (currentEditingCategory == null) {
            Category newCat = new Category();
            newCat.setName(name); newCat.setParentId(parentId);
            if (catDao.addCategory(newCat)) {
                refreshCategoryData(); loadMainCats();
                txtCategoryName.clear();
                lblStatusBar.setText("Категория успешно добавлена");
            }
        } else {
            currentEditingCategory.setName(name);
            currentEditingCategory.setParentId(parentId);
            if (catDao.updateCategory(currentEditingCategory)) {
                refreshCategoryData(); loadMainCats();
                currentEditingCategory = null;
                txtCategoryName.clear();
                lblStatusBar.setText("Категория успешно обновлена");
            }
        }
    }

    private void loadParentCategoryCombo() {
        cbParentCategory.getItems().clear();
        Category noParent = new Category();
        noParent.setId(0); noParent.setName("--- Нет (Сделать главной) ---");
        cbParentCategory.getItems().add(noParent);
        cbParentCategory.getItems().addAll(catDao.getMainCategories());
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

    private void setupCategoryBoxes() {
        cbMainCat.setConverter(new StringConverter<Category>() {
            @Override public String toString(Category c) { return c == null ? "" : c.getName(); }
            @Override public Category fromString(String s) {
                if (s == null || s.trim().isEmpty()) return null;
                for (Category c : cbMainCat.getItems()) if (c.getName().equalsIgnoreCase(s.trim())) return c;
                Category temp = new Category(); temp.setId(0); temp.setName(s.trim()); return temp;
            }
        });

        cbSubCat.setConverter(new StringConverter<Category>() {
            @Override public String toString(Category c) { return c == null ? "" : c.getName(); }
            @Override public Category fromString(String s) {
                if (s == null || s.trim().isEmpty()) return null;
                for (Category c : cbSubCat.getItems()) if (c.getName().equalsIgnoreCase(s.trim())) return c;
                Category temp = new Category(); temp.setId(0); temp.setName(s.trim()); return temp;
            }
        });

        cbMainCat.valueProperty().addListener((obs, oldVal, newVal) -> {
            cbSubCat.getItems().clear();
            if (newVal != null && newVal.getId() > 0) cbSubCat.getItems().addAll(catDao.getSubCategories(newVal.getId()));
        });

        cbMainCat.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                lblMainCatSug.setVisible(false); lblMainCatSug.setManaged(false);
                btnFastMainCat.setVisible(false); btnFastMainCat.setManaged(false);
                return;
            }
            boolean found = false;
            for (int i = 0; i < cbMainCat.getItems().size(); i++) {
                if (cbMainCat.getItems().get(i).getName().equalsIgnoreCase(newText.trim())) { found = true; break; }
            }
            lblMainCatSug.setVisible(!found); lblMainCatSug.setManaged(!found);
            btnFastMainCat.setVisible(!found); btnFastMainCat.setManaged(!found);
        });

        btnFastMainCat.setOnAction(e -> {
            String s = cbMainCat.getEditor().getText().trim();
            Category c = new Category(); c.setName(s); c.setParentId(null);
            if (catDao.addCategory(c)) {
                loadMainCats();
                for (int i = 0; i < cbMainCat.getItems().size(); i++) {
                    if (cbMainCat.getItems().get(i).getName().equals(s)) { cbMainCat.getSelectionModel().select(i); break; }
                }
                lblMainCatSug.setVisible(false); lblMainCatSug.setManaged(false);
                btnFastMainCat.setVisible(false); btnFastMainCat.setManaged(false);
                lblStatusBar.setText("Создана главная категория: " + s);
            }
        });

        cbSubCat.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                lblSubCatSug.setVisible(false); lblSubCatSug.setManaged(false);
                btnFastSubCat.setVisible(false); btnFastSubCat.setManaged(false);
                return;
            }
            boolean found = false;
            for (int i = 0; i < cbSubCat.getItems().size(); i++) {
                if (cbSubCat.getItems().get(i).getName().equalsIgnoreCase(newText.trim())) { found = true; break; }
            }
            lblSubCatSug.setVisible(!found); lblSubCatSug.setManaged(!found);
            btnFastSubCat.setVisible(!found); btnFastSubCat.setManaged(!found);
        });

        btnFastSubCat.setOnAction(e -> {
            Category m = cbMainCat.getSelectionModel().getSelectedItem();
            if (m == null || m.getId() == 0) {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Сначала выберите или создайте главную категорию!");
                return;
            }
            String s = cbSubCat.getEditor().getText().trim();
            Category c = new Category(); c.setName(s); c.setParentId(m.getId());
            if (catDao.addCategory(c)) {
                cbSubCat.getItems().clear();
                cbSubCat.getItems().addAll(catDao.getSubCategories(m.getId()));
                for (int i = 0; i < cbSubCat.getItems().size(); i++) {
                    if (cbSubCat.getItems().get(i).getName().equals(s)) { cbSubCat.getSelectionModel().select(i); break; }
                }
                lblSubCatSug.setVisible(false); lblSubCatSug.setManaged(false);
                btnFastSubCat.setVisible(false); btnFastSubCat.setManaged(false);
                lblStatusBar.setText("Создана подкатегория: " + s);
            }
        });
    }

    private void loadMainCats() {
        cbMainCat.getItems().clear();
        cbMainCat.getItems().addAll(catDao.getMainCategories());
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

    private void initStatusBar() {
        if (lblStatusBar != null) lblStatusBar.setText(LocalizationManager.getString("status.ready"));
    }

    private void initCompanyTab() {
        colCompanyName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCompanyINN.setCellValueFactory(new PropertyValueFactory<>("inn"));
        colCompanyRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colCompanyNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colCompanyAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadCompaniesData();

        btnAddCompany.setOnAction(event -> {
            Optional<Company> result = showCompanyDialog(null);
            result.ifPresent(newCompany -> {
                if (companyDAO.addCompany(newCompany)) {
                    loadCompaniesData();
                    lblStatusBar.setText("Фирма успешно добавлена: " + newCompany.getName());
                } else lblStatusBar.setText("Ошибка при добавлении фирмы (возможно такой ИНН уже есть).");
            });
        });

        btnEditCompany.setOnAction(event -> {
            Company selectedCompany = tableCompanies.getSelectionModel().getSelectedItem();
            if (selectedCompany != null) {
                Optional<Company> result = showCompanyDialog(selectedCompany);
                result.ifPresent(updatedCompany -> {
                    if (companyDAO.updateCompany(updatedCompany)) {
                        tableCompanies.refresh();
                        lblStatusBar.setText("Данные фирмы обновлены.");
                    } else lblStatusBar.setText("Ошибка при обновлении фирмы.");
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Внимание"); alert.setHeaderText("Фирма не выбрана");
                alert.setContentText("Пожалуйста, выберите фирму в таблице для редактирования.");
                alert.showAndWait();
            }
        });

        btnDeleteCompany.setOnAction(event -> {
            Company selectedCompany = tableCompanies.getSelectionModel().getSelectedItem();
            if (selectedCompany != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Подтверждение удаления"); confirm.setHeaderText("Удаление фирмы");
                confirm.setContentText("Вы действительно хотите удалить фирму: " + selectedCompany.getName() + "?");
                Optional<ButtonType> alertResult = confirm.showAndWait();
                if (alertResult.isPresent() && alertResult.get() == ButtonType.OK) {
                    if (companyDAO.deleteCompany(selectedCompany.getId())) {
                        companyData.remove(selectedCompany);
                        lblStatusBar.setText("Фирма успешно удалена.");
                    } else lblStatusBar.setText("Ошибка при удалении фирмы.");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Внимание"); alert.setHeaderText("Фирма не выбрана");
                alert.setContentText("Пожалуйста, выберите фирму в таблице для удаления.");
                alert.showAndWait();
            }
        });
    }

    private void loadCompaniesData() {
        companyData.clear();
        companyData.addAll(companyDAO.getAllCompanies());
        tableCompanies.setItems(companyData);
    }

    private Optional<Company> showCompanyDialog(Company companyToEdit) {
        Dialog<Company> dialog = new Dialog<>();
        dialog.setTitle(companyToEdit == null ? "Добавить фирму" : "Редактировать фирму");
        dialog.setHeaderText("Заполните данные о фирме");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Название");
        TextField addressField = new TextField(); addressField.setPromptText("Адрес");

        TextField ratingField = new TextField(); ratingField.setPromptText("От 0.0 до 5.0");
        UnaryOperator<TextFormatter.Change> ratingFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,1}([.,]\\d{0,2})?")) return change;
            return null;
        };
        ratingField.setTextFormatter(new TextFormatter<>(ratingFilter));

        TextField innField = new TextField(); innField.setPromptText("10 или 12 цифр");
        UnaryOperator<TextFormatter.Change> numberFilter = change -> {
            if (change.getControlNewText().matches("\\d*")) return change;
            return null;
        };
        innField.setTextFormatter(new TextFormatter<>(numberFilter));

        TextField phoneField = new TextField(); phoneField.setPromptText("9991234567");
        UnaryOperator<TextFormatter.Change> phoneFilter = change -> {
            if (change.getControlNewText().matches("\\d{0,10}")) return change;
            return null;
        };
        phoneField.setTextFormatter(new TextFormatter<>(phoneFilter));

        HBox phoneBox = new HBox(5, new Label("+7"), phoneField);
        phoneBox.setAlignment(Pos.CENTER_LEFT);

        if (companyToEdit != null) {
            nameField.setText(companyToEdit.getName());
            innField.setText(companyToEdit.getInn());
            ratingField.setText(companyToEdit.getRating() != null ? companyToEdit.getRating().toString() : "0.00");
            addressField.setText(companyToEdit.getAddress());
            String p = companyToEdit.getPhone();
            if (p != null && p.startsWith("+7")) phoneField.setText(p.substring(2));
            else phoneField.setText(p);
        }

        grid.add(new Label("Название:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("ИНН:"), 0, 1);      grid.add(innField, 1, 1);
        grid.add(new Label("Рейтинг:"), 0, 2);  grid.add(ratingField, 1, 2);
        grid.add(new Label("Телефон:"), 0, 3);  grid.add(phoneBox, 1, 3);
        grid.add(new Label("Адрес:"), 0, 4);    grid.add(addressField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        final Button btSave = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        btSave.addEventFilter(ActionEvent.ACTION, event -> {
            boolean isValid = true;
            String errorStyle = "-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 3px;";
            String defaultStyle = "";

            if (nameField.getText().trim().isEmpty()) { nameField.setStyle(errorStyle); isValid = false; }
            else nameField.setStyle(defaultStyle);

            if (addressField.getText().trim().isEmpty()) { addressField.setStyle(errorStyle); isValid = false; }
            else addressField.setStyle(defaultStyle);

            String inn = innField.getText();
            if (inn.length() != 10 && inn.length() != 12) { innField.setStyle(errorStyle); isValid = false; }
            else innField.setStyle(defaultStyle);

            if (phoneField.getText().length() != 10) { phoneField.setStyle(errorStyle); isValid = false; }
            else phoneField.setStyle(defaultStyle);

            try {
                String ratingStr = ratingField.getText().replace(",", ".");
                if (!ratingStr.isEmpty()) {
                    BigDecimal r = new BigDecimal(ratingStr);
                    if (r.compareTo(BigDecimal.ZERO) < 0 || r.compareTo(new BigDecimal("5.00")) > 0) {
                        ratingField.setStyle(errorStyle); isValid = false;
                    } else ratingField.setStyle(defaultStyle);
                } else ratingField.setStyle(defaultStyle);
            } catch (NumberFormatException e) {
                ratingField.setStyle(errorStyle); isValid = false;
            }

            if (!isValid) {
                event.consume();
                lblStatusBar.setText("ОШИБКА: Заполните корректно все выделенные красным поля!");
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String ratingStr = ratingField.getText().replace(",", ".");
                BigDecimal rating = ratingStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(ratingStr);
                String fullPhone = "+7" + phoneField.getText();

                if (companyToEdit == null) return new Company(nameField.getText(), innField.getText(), rating, fullPhone, addressField.getText());
                else {
                    companyToEdit.setName(nameField.getText());
                    companyToEdit.setInn(innField.getText());
                    companyToEdit.setRating(rating);
                    companyToEdit.setPhone(fullPhone);
                    companyToEdit.setAddress(addressField.getText());
                    return companyToEdit;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void initProductTab() {
        loadDictionaries();

        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProductArticle.setCellValueFactory(new PropertyValueFactory<>("articleNumber"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colProductBrand.setCellValueFactory(new PropertyValueFactory<>("brandId"));
        colProductBrand.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null || id == 0) setText(null);
                else setText(brandMap.getOrDefault(id, "Неизвестно"));
            }
        });

        colProductUnit.setCellValueFactory(new PropertyValueFactory<>("unitId"));
        colProductUnit.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null || id == 0) setText(null);
                else setText(unitMap.getOrDefault(id, "Неизвестно"));
            }
        });

        setupComboBoxes();
        setupCategoryBoxes();
        setupProductButtons();
        loadProductsData();

        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentEditingProduct = newSelection;
                fillProductForm(newSelection);
                showProductForm(true);
            } else {
                currentEditingProduct = null;
                clearProductForm();
            }
        });

        showProductForm(false);
    }

    private void loadDictionaries() {
        cbProductBrand.getItems().clear(); brandMap.clear();
        for (Brand b : brandDAO.getAllBrands()) {
            cbProductBrand.getItems().add(b);
            brandMap.put(b.getId(), b.getName());
        }

        cbProductUnit.getItems().clear(); unitMap.clear();
        for (UnitOfMeasure u : unitDAO.getAllUnits()) {
            cbProductUnit.getItems().add(u);
            unitMap.put(u.getId(), u.getName());
        }
    }

    private void setupComboBoxes() {
        cbProductBrand.setConverter(new StringConverter<Brand>() {
            @Override public String toString(Brand b) { return b == null ? "" : b.getName(); }
            @Override public Brand fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;
                for (Brand b : cbProductBrand.getItems()) if (b.getName().equalsIgnoreCase(string.trim())) return b;
                return new Brand(0, string.trim());
            }
        });

        cbProductUnit.setConverter(new StringConverter<UnitOfMeasure>() {
            @Override public String toString(UnitOfMeasure u) { return u == null ? "" : u.getName(); }
            @Override public UnitOfMeasure fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;
                for (UnitOfMeasure u : cbProductUnit.getItems()) if (u.getName().equalsIgnoreCase(string.trim())) return u;
                return new UnitOfMeasure(0, string.trim());
            }
        });

        cbProductBrand.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                btnFastAddBrand.setVisible(false); btnFastAddBrand.setManaged(false);
                lblBrandSuggestion.setVisible(false); lblBrandSuggestion.setManaged(false);
                return;
            }
            boolean found = false;
            for (int i = 0; i < cbProductBrand.getItems().size(); i++) {
                if (cbProductBrand.getItems().get(i).getName().equalsIgnoreCase(newText.trim())) { found = true; break; }
            }
            if (!found) {
                lblBrandSuggestion.setVisible(true); lblBrandSuggestion.setManaged(true);
                btnFastAddBrand.setVisible(true); btnFastAddBrand.setManaged(true);
            } else {
                lblBrandSuggestion.setVisible(false); lblBrandSuggestion.setManaged(false);
                btnFastAddBrand.setVisible(false); btnFastAddBrand.setManaged(false);
            }
        });

        btnFastAddBrand.setOnAction(e -> {
            String name = cbProductBrand.getEditor().getText().trim();
            Brand b = new Brand(name);
            if (brandDAO.addBrand(b)) {
                loadDictionaries();
                for (int i = 0; i < cbProductBrand.getItems().size(); i++) {
                    if (cbProductBrand.getItems().get(i).getName().equals(name)) { cbProductBrand.getSelectionModel().select(i); break; }
                }
                btnFastAddBrand.setVisible(false); btnFastAddBrand.setManaged(false);
                lblBrandSuggestion.setVisible(false); lblBrandSuggestion.setManaged(false);
                loadReferencesData();
                lblStatusBar.setText("Новый бренд сохранен: " + name);
            }
        });

        cbProductUnit.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                btnFastAddUnit.setVisible(false); btnFastAddUnit.setManaged(false);
                lblUnitSuggestion.setVisible(false); lblUnitSuggestion.setManaged(false);
                return;
            }
            boolean found = false;
            for (int i = 0; i < cbProductUnit.getItems().size(); i++) {
                if (cbProductUnit.getItems().get(i).getName().equalsIgnoreCase(newText.trim())) { found = true; break; }
            }
            if (!found) {
                lblUnitSuggestion.setVisible(true); lblUnitSuggestion.setManaged(true);
                btnFastAddUnit.setVisible(true); btnFastAddUnit.setManaged(true);
            } else {
                lblUnitSuggestion.setVisible(false); lblUnitSuggestion.setManaged(false);
                btnFastAddUnit.setVisible(false); btnFastAddUnit.setManaged(false);
            }
        });

        btnFastAddUnit.setOnAction(e -> {
            String name = cbProductUnit.getEditor().getText().trim();
            UnitOfMeasure u = new UnitOfMeasure(name);
            if (unitDAO.addUnit(u)) {
                loadDictionaries();
                for (int i = 0; i < cbProductUnit.getItems().size(); i++) {
                    if (cbProductUnit.getItems().get(i).getName().equals(name)) { cbProductUnit.getSelectionModel().select(i); break; }
                }
                btnFastAddUnit.setVisible(false); btnFastAddUnit.setManaged(false);
                lblUnitSuggestion.setVisible(false); lblUnitSuggestion.setManaged(false);
                loadReferencesData();
                lblStatusBar.setText("Новая ед. изм. сохранена: " + name);
            }
        });
    }

    private void loadProductsData() {
        productData.clear();
        productData.addAll(productDAO.getAllProducts());
        tableProducts.setItems(productData);
    }

    private void setupProductButtons() {
        btnRefreshProduct.setOnAction(e -> {
            loadDictionaries(); loadProductsData();
            lblStatusBar.setText("Данные товаров обновлены");
        });

        btnAddProduct.setOnAction(e -> {
            tableProducts.getSelectionModel().clearSelection();
            showProductForm(true);
            lblStatusBar.setText("Режим добавления нового товара");
        });

        btnEditProduct.setOnAction(e -> {
            Product selected = tableProducts.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showProductForm(true);
                lblStatusBar.setText("Режим редактирования товара");
            } else {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите товар для редактирования");
            }
        });

        btnCancelProduct.setOnAction(e -> {
            tableProducts.getSelectionModel().clearSelection();
            lblStatusBar.setText("Операция отменена");
        });

        btnDeleteProduct.setOnAction(e -> {
            Product selected = tableProducts.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить товар " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        if (productDAO.deleteProduct(selected.getId())) {
                            productData.remove(selected);
                            tableProducts.getSelectionModel().clearSelection();
                            lblStatusBar.setText("Товар удален");
                        } else showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить товар");
                    }
                });
            } else showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите товар для удаления");
        });

        btnSaveProduct.setOnAction(e -> saveProductAction());
        loadDictionaries(); loadProductsData();

        btnChooseImg.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Выберите фото товара");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg"));
            File f = fc.showOpenDialog(imgProduct.getScene().getWindow());
            if (f != null) {
                try {
                    imgBytes = Files.readAllBytes(f.toPath());
                    Image img = new Image(new ByteArrayInputStream(imgBytes));
                    imgProduct.setImage(img);
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить фото");
                }
            }
        });
    }

    private void showProductForm(boolean show) {
        productFormPane.setVisible(true);
        productFormPane.setManaged(true);

        if (show) {
            productFormPane.setExpanded(true);
        }
    }

    private void clearProductForm() {
        txtProductName.clear(); loadMainCats(); txtProductCharacteristic.clear();
        txtProductPrice.clear(); txtProductDiscount.setText("0.00"); txtProductVat.setText("20.00");
        cbProductBrand.getSelectionModel().clearSelection();
        cbProductUnit.getSelectionModel().clearSelection();
        imgBytes = null; imgProduct.setImage(null);
    }

    private void fillProductForm(Product p) {
        txtProductName.setText(p.getName());
        txtProductCharacteristic.setText(p.getCharacteristic());
        txtProductPrice.setText(p.getPrice() != null ? p.getPrice().toString() : "0.00");
        txtProductDiscount.setText(p.getDiscount() != null ? p.getDiscount().toString() : "0.00");
        txtProductVat.setText(p.getVat() != null ? p.getVat().toString() : "20.00");

        // Загружаем изображение
        imgBytes = p.getImage();
        if (imgBytes != null && imgBytes.length > 0) {
            Image img = new Image(new ByteArrayInputStream(imgBytes));
            imgProduct.setImage(img);
        } else {
            imgProduct.setImage(null);
        }

        // Выбираем бренд
        cbProductBrand.getItems().stream()
                .filter(b -> b.getId() == p.getBrandId())
                .findFirst()
                .ifPresent(cbProductBrand.getSelectionModel()::select);

        // Выбираем единицу измерения
        cbProductUnit.getItems().stream()
                .filter(u -> u.getId() == p.getUnitId())
                .findFirst()
                .ifPresent(cbProductUnit.getSelectionModel()::select);

        // === НОВОЕ: Загружаем и выбираем категории ===
        loadMainCats(); // Сначала загружаем главные категории

        if (p.getCategoryId() != null && p.getCategoryId() > 0) {
            // Ищем и выбираем главную категорию
            cbMainCat.getItems().stream()
                    .filter(c -> c.getId() == p.getCategoryId())
                    .findFirst()
                    .ifPresent(mainCat -> {
                        cbMainCat.getSelectionModel().select(mainCat);

                        // Если есть подкатегория, загружаем и выбираем её
                        if (p.getSubcategoryId() != null && p.getSubcategoryId() > 0) {
                            // Загружаем подкатегории для выбранной главной
                            cbSubCat.getItems().clear();
                            cbSubCat.getItems().addAll(catDao.getSubCategories(mainCat.getId()));

                            // Выбираем подкатегорию
                            cbSubCat.getItems().stream()
                                    .filter(sub -> sub.getId() == p.getSubcategoryId())
                                    .findFirst()
                                    .ifPresent(cbSubCat.getSelectionModel()::select);
                        }
                    });
        }
    }

    private void saveProductAction() {
        try {
            BigDecimal price = new BigDecimal(txtProductPrice.getText().replace(",", ".").trim());
            BigDecimal discount = new BigDecimal(txtProductDiscount.getText().replace(",", ".").trim());
            BigDecimal vat = new BigDecimal(txtProductVat.getText().replace(",", ".").trim());

            Brand selectedBrand = cbProductBrand.getSelectionModel().getSelectedItem();
            UnitOfMeasure selectedUnit = cbProductUnit.getSelectionModel().getSelectedItem();

            int brandId = selectedBrand != null ? selectedBrand.getId() : 0;
            int unitId = selectedUnit != null ? selectedUnit.getId() : 0;

            Category selectedMainCat = cbMainCat.getSelectionModel().getSelectedItem();
            Category selectedSubCat = cbSubCat.getSelectionModel().getSelectedItem();

            Integer categoryId = (selectedMainCat != null && selectedMainCat.getId() > 0)
                    ? selectedMainCat.getId() : null;
            Integer subcategoryId = (selectedSubCat != null && selectedSubCat.getId() > 0)
                    ? selectedSubCat.getId() : null;

            if (currentEditingProduct == null) {
                // ДОБАВЛЕНИЕ
                String newArt = productDAO.generateNewArticle(categoryId != null ? categoryId : 0);
                Product newProduct = new Product(
                        0, txtProductName.getText(), newArt, price,
                        txtProductCharacteristic.getText(), discount, vat,
                        brandId, unitId, categoryId, subcategoryId, imgBytes
                );
                if (productDAO.addProduct(newProduct)) {
                    loadProductsData();
                    showProductForm(false);
                    lblStatusBar.setText("Товар добавлен");
                }
            } else {
                // ОБНОВЛЕНИЕ
                BigDecimal oldPrice = currentEditingProduct.getPrice();
                System.out.println("=== ИЗМЕНЕНИЕ ЦЕНЫ ===");
                System.out.println("Старая цена: " + oldPrice);
                System.out.println("Новая цена: " + price);
                System.out.println("Цены равны? " + (oldPrice.compareTo(price) == 0));

                currentEditingProduct.setName(txtProductName.getText());
                currentEditingProduct.setCharacteristic(txtProductCharacteristic.getText());
                currentEditingProduct.setPrice(price);
                currentEditingProduct.setDiscount(discount);
                currentEditingProduct.setVat(vat);
                currentEditingProduct.setBrandId(brandId);
                currentEditingProduct.setUnitId(unitId);
                currentEditingProduct.setCategoryId(categoryId);
                currentEditingProduct.setSubcategoryId(subcategoryId);
                currentEditingProduct.setImage(imgBytes);

                if (productDAO.updateProduct(currentEditingProduct)) {
                    System.out.println("Товар обновлен в БД");

                    // === ЛОГИРУЕМ ИЗМЕНЕНИЕ ЦЕНЫ ===
                    if (oldPrice != null && price != null && oldPrice.compareTo(price) != 0) {
                        System.out.println("Цена изменилась, записываем в историю...");
                        PriceHistoryDAO priceHistoryDAO = new PriceHistoryDAO();
                        priceHistoryDAO.logPriceChange(
                                currentEditingProduct.getId(),
                                oldPrice,
                                price
                        );
                    } else {
                        System.out.println("Цена не изменилась или null");
                    }
                    // =================================

                    tableProducts.refresh();
                    showProductForm(false);
                    lblStatusBar.setText("Товар обновлен");
                } else {
                    System.out.println("НЕ УДАЛОСЬ обновить товар в БД");
                }
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Ошибка ввода",
                    "Проверьте правильность числовых полей (Цена, Скидка, НДС)");
            ex.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }

    private void initReferencesTab() {
        listBrands.setCellFactory(param -> new javafx.scene.control.ListCell<Brand>() {
            @Override
            protected void updateItem(Brand b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null : b.getName());
            }
        });

        listUnits.setCellFactory(param -> new javafx.scene.control.ListCell<UnitOfMeasure>() {
            @Override
            protected void updateItem(UnitOfMeasure u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getName());
            }
        });

        // Кнопка Добавить бренд
        btnAddBrand.setOnAction(e -> {
            String name = txtBrandName.getText().trim();
            if (!name.isEmpty()) {
                if (brandDAO.addBrand(new Brand(name))) {
                    txtBrandName.clear();
                    loadDictionaries();
                    loadReferencesData();
                    lblStatusBar.setText("Бренд добавлен: " + name);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить бренд");
                }
            }
        });

        // Кнопка Удалить бренд
        btnDeleteBrand.setOnAction(e -> {
            Brand selected = listBrands.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Удалить бренд '" + selected.getName() + "'?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Подтверждение удаления");
                confirm.setHeaderText(null);

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        if (brandDAO.deleteBrand(selected.getId())) {
                            loadDictionaries();
                            loadReferencesData();
                            lblStatusBar.setText("Бренд удален: " + selected.getName());
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Ошибка",
                                    "Не удалось удалить бренд. Возможно, он используется в товарах.");
                        }
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите бренд для удаления!");
            }
        });

        // Кнопка Добавить единицу измерения
        btnAddUnit.setOnAction(e -> {
            String name = txtUnitName.getText().trim();
            if (!name.isEmpty()) {
                if (unitDAO.addUnit(new UnitOfMeasure(name))) {
                    txtUnitName.clear();
                    loadDictionaries();
                    loadReferencesData();
                    lblStatusBar.setText("Единица измерения добавлена: " + name);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить единицу измерения");
                }
            }
        });

        // Кнопка Удалить единицу измерения
        btnDeleteUnit.setOnAction(e -> {
            UnitOfMeasure selected = listUnits.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Удалить единицу измерения '" + selected.getName() + "'?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Подтверждение удаления");
                confirm.setHeaderText(null);

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        if (unitDAO.deleteUnit(selected.getId())) {
                            loadDictionaries();
                            loadReferencesData();
                            lblStatusBar.setText("Единица измерения удалена: " + selected.getName());
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Ошибка",
                                    "Не удалось удалить единицу измерения. Возможно, она используется в товарах.");
                        }
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите единицу измерения для удаления!");
            }
        });

        loadReferencesData();
    }

    private void loadReferencesData() {
        listBrands.getItems().clear();
        listBrands.getItems().addAll(brandDAO.getAllBrands());

        listUnits.getItems().clear();
        listUnits.getItems().addAll(unitDAO.getAllUnits());
    }
}