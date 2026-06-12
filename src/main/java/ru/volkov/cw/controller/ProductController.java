package ru.volkov.cw.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import ru.volkov.cw.dao.*;
import ru.volkov.cw.model.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class ProductController {
    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, String> colProductName, colProductArticle;
    @FXML private TableColumn<Product, BigDecimal> colProductPrice;
    @FXML private TableColumn<Product, Integer> colProductBrand, colProductUnit;
    @FXML private TitledPane productFormPane;
    @FXML private TextField txtProductName, txtProductCharacteristic, txtProductPrice, txtProductDiscount, txtProductVat;
    @FXML private ComboBox<Brand> cbProductBrand;
    @FXML private ComboBox<UnitOfMeasure> cbProductUnit;
    @FXML private Button btnAddProduct, btnEditProduct, btnDeleteProduct, btnRefreshProduct, btnSaveProduct, btnCancelProduct, btnChooseImg, btnFastAddBrand, btnFastAddUnit;
    @FXML private ImageView imgProduct;
    @FXML private Label lblBrandSuggestion, lblUnitSuggestion;
    @FXML private ComboBox<Category> cbMainCat, cbSubCat;
    @FXML private Button btnFastMainCat, btnFastSubCat;
    @FXML private Label lblMainCatSug, lblSubCatSug;
    @FXML private TextField txtSearchProduct;
    @FXML private ComboBox<Category> cbFilterCategory;

    private byte[] imgBytes = null;
    private FilteredList<Product> filteredProducts;
    private final ProductDAO productDAO = new ProductDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final UnitOfMeasureDAO unitDAO = new UnitOfMeasureDAO();
    private final CategoryDAO catDao = new CategoryDAO();
    private ObservableList<Product> productData = FXCollections.observableArrayList();
    private Map<Integer, String> brandMap = new HashMap<>();
    private Map<Integer, String> unitMap = new HashMap<>();
    private Product currentEditingProduct = null;
    private Consumer<String> statusCallback;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

    @FXML public void initialize() { initProductTab(); }

    private void initProductTab() {
        loadDictionaries();
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProductArticle.setCellValueFactory(new PropertyValueFactory<>("articleNumber"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProductBrand.setCellValueFactory(new PropertyValueFactory<>("brandId"));
        colProductBrand.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty); setText(empty || id == null || id == 0 ? null : brandMap.getOrDefault(id, "?"));
            }
        });
        colProductUnit.setCellValueFactory(new PropertyValueFactory<>("unitId"));
        colProductUnit.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty); setText(empty || id == null || id == 0 ? null : unitMap.getOrDefault(id, "?"));
            }
        });

        setupComboBoxes(); setupCategoryBoxes(); setupProductButtons(); loadProductsData(); initProductFilters();

        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) { currentEditingProduct = newVal; fillProductForm(newVal); showProductForm(true); }
            else { currentEditingProduct = null; clearProductForm(); }
        });
        showProductForm(false);
    }

    public void loadDictionaries() {
        cbProductBrand.getItems().clear(); brandMap.clear();
        for (Brand b : brandDAO.getAllBrands()) { cbProductBrand.getItems().add(b); brandMap.put(b.getId(), b.getName()); }
        cbProductUnit.getItems().clear(); unitMap.clear();
        for (UnitOfMeasure u : unitDAO.getAllUnits()) { cbProductUnit.getItems().add(u); unitMap.put(u.getId(), u.getName()); }
    }

    private void setupComboBoxes() {
        cbProductBrand.setConverter(new StringConverter<>() {
            @Override public String toString(Brand b) { return b == null ? "" : b.getName(); }
            @Override public Brand fromString(String s) { return null; }
        });
        cbProductUnit.setConverter(new StringConverter<>() {
            @Override public String toString(UnitOfMeasure u) { return u == null ? "" : u.getName(); }
            @Override public UnitOfMeasure fromString(String s) { return null; }
        });

        cbProductBrand.getEditor().textProperty().addListener((obs, o, n) -> {
            boolean found = cbProductBrand.getItems().stream().anyMatch(b -> b.getName().equalsIgnoreCase(n));
            btnFastAddBrand.setVisible(!found); btnFastAddBrand.setManaged(!found);
            lblBrandSuggestion.setVisible(!found); lblBrandSuggestion.setManaged(!found);
        });
        btnFastAddBrand.setOnAction(e -> {
            String name = cbProductBrand.getEditor().getText().trim();
            if (brandDAO.addBrand(new Brand(name))) { loadDictionaries(); updateStatus("Бренд сохранен: " + name); }
        });

        cbProductUnit.getEditor().textProperty().addListener((obs, o, n) -> {
            boolean found = cbProductUnit.getItems().stream().anyMatch(u -> u.getName().equalsIgnoreCase(n));
            btnFastAddUnit.setVisible(!found); btnFastAddUnit.setManaged(!found);
            lblUnitSuggestion.setVisible(!found); lblUnitSuggestion.setManaged(!found);
        });
        btnFastAddUnit.setOnAction(e -> {
            String name = cbProductUnit.getEditor().getText().trim();
            if (unitDAO.addUnit(new UnitOfMeasure(name))) { loadDictionaries(); updateStatus("Ед. изм. сохранена: " + name); }
        });
    }

    private void setupCategoryBoxes() {
        cbMainCat.setConverter(new StringConverter<>() { @Override public String toString(Category c) { return c == null ? "" : c.getName(); } @Override public Category fromString(String s) { return null; } });
        cbSubCat.setConverter(new StringConverter<>() { @Override public String toString(Category c) { return c == null ? "" : c.getName(); } @Override public Category fromString(String s) { return null; } });
        cbMainCat.valueProperty().addListener((obs, o, n) -> {
            cbSubCat.getItems().clear();
            if (n != null && n.getId() > 0) cbSubCat.getItems().addAll(catDao.getSubCategories(n.getId()));
        });
        btnFastMainCat.setOnAction(e -> {
            String s = cbMainCat.getEditor().getText().trim();
            if (catDao.addCategory(new Category(s, null))) { loadMainCats(); updateStatus("Категория создана: " + s); }
        });
        btnFastSubCat.setOnAction(e -> {
            Category m = cbMainCat.getSelectionModel().getSelectedItem();
            if (m != null && m.getId() > 0) {
                String s = cbSubCat.getEditor().getText().trim();
                if (catDao.addCategory(new Category(s, m.getId()))) { updateStatus("Подкатегория создана: " + s); }
            }
        });
    }

    public void loadMainCats() { cbMainCat.getItems().clear(); cbMainCat.getItems().addAll(catDao.getMainCategories()); }

    public void refreshCategoryFilters() {
        Category sel = cbFilterCategory.getValue();
        cbFilterCategory.getItems().clear();
        cbFilterCategory.getItems().add(null);
        cbFilterCategory.getItems().addAll(catDao.getAllCategories());
        if(sel != null) cbFilterCategory.setValue(sel);
    }

    private void initProductFilters() {
        cbFilterCategory.getItems().add(null);
        cbFilterCategory.getItems().addAll(catDao.getAllCategories());
        cbFilterCategory.setConverter(new StringConverter<>() {
            @Override public String toString(Category c) { return c == null ? "Все категории" : c.getName(); }
            @Override public Category fromString(String s) { return null; }
        });
        filteredProducts = new FilteredList<>(productData, p -> true);
        tableProducts.setItems(filteredProducts);
        Runnable applyFilter = () -> {
            String search = txtSearchProduct.getText().toLowerCase();
            Category cat = cbFilterCategory.getValue();
            filteredProducts.setPredicate(p -> {
                boolean matchName = search.isEmpty() || p.getName().toLowerCase().contains(search);
                boolean matchCat = (cat == null) || (p.getCategoryId() != null && p.getCategoryId() == cat.getId());
                return matchName && matchCat;
            });
        };
        txtSearchProduct.textProperty().addListener(obs -> applyFilter.run());
        cbFilterCategory.valueProperty().addListener(obs -> applyFilter.run());
    }

    private void loadProductsData() {
        productData.clear(); productData.addAll(productDAO.getAllProducts()); tableProducts.setItems(productData);
    }

    private void setupProductButtons() {
        btnRefreshProduct.setOnAction(e -> { loadDictionaries(); loadProductsData(); updateStatus("Данные обновлены"); });
        btnAddProduct.setOnAction(e -> { tableProducts.getSelectionModel().clearSelection(); showProductForm(true); });
        btnEditProduct.setOnAction(e -> { if (tableProducts.getSelectionModel().getSelectedItem() != null) showProductForm(true); });
        btnCancelProduct.setOnAction(e -> tableProducts.getSelectionModel().clearSelection());
        btnDeleteProduct.setOnAction(e -> {
            Product sel = tableProducts.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && productDAO.deleteProduct(sel.getId())) { productData.remove(sel); updateStatus("Удалено"); } });
            }
        });
        btnSaveProduct.setOnAction(e -> saveProductAction());
        btnChooseImg.setOnAction(e -> {
            File f = new FileChooser().showOpenDialog(imgProduct.getScene().getWindow());
            if (f != null) try { imgBytes = Files.readAllBytes(f.toPath()); imgProduct.setImage(new Image(new ByteArrayInputStream(imgBytes))); } catch (Exception ex) {}
        });
    }

    private void showProductForm(boolean show) { if (show) productFormPane.setExpanded(true); }
    private void clearProductForm() {
        txtProductName.clear(); txtProductCharacteristic.clear(); txtProductPrice.clear();
        txtProductDiscount.setText("0.00"); txtProductVat.setText("20.00");
        cbProductBrand.getSelectionModel().clearSelection(); cbProductUnit.getSelectionModel().clearSelection();
        imgBytes = null; imgProduct.setImage(null);
    }

    private void fillProductForm(Product p) {
        txtProductName.setText(p.getName()); txtProductCharacteristic.setText(p.getCharacteristic());
        txtProductPrice.setText(p.getPrice() != null ? p.getPrice().toString() : "0.00");
        txtProductDiscount.setText(p.getDiscount() != null ? p.getDiscount().toString() : "0.00");
        txtProductVat.setText(p.getVat() != null ? p.getVat().toString() : "20.00");
        imgBytes = p.getImage();
        if (imgBytes != null && imgBytes.length > 0) imgProduct.setImage(new Image(new ByteArrayInputStream(imgBytes)));
        else imgProduct.setImage(null);

        cbProductBrand.getItems().stream().filter(b -> b.getId() == p.getBrandId()).findFirst().ifPresent(cbProductBrand.getSelectionModel()::select);
        cbProductUnit.getItems().stream().filter(u -> u.getId() == p.getUnitId()).findFirst().ifPresent(cbProductUnit.getSelectionModel()::select);

        loadMainCats();
        if (p.getCategoryId() != null && p.getCategoryId() > 0) {
            cbMainCat.getItems().stream().filter(c -> c.getId() == p.getCategoryId()).findFirst().ifPresent(mainCat -> {
                cbMainCat.getSelectionModel().select(mainCat);
                if (p.getSubcategoryId() != null && p.getSubcategoryId() > 0) {
                    cbSubCat.getItems().clear(); cbSubCat.getItems().addAll(catDao.getSubCategories(mainCat.getId()));
                    cbSubCat.getItems().stream().filter(sub -> sub.getId() == p.getSubcategoryId()).findFirst().ifPresent(cbSubCat.getSelectionModel()::select);
                }
            });
        }
    }

    private void saveProductAction() {
        try {
            BigDecimal price = new BigDecimal(txtProductPrice.getText().replace(",", ".").trim());
            BigDecimal discount = new BigDecimal(txtProductDiscount.getText().replace(",", ".").trim());
            BigDecimal vat = new BigDecimal(txtProductVat.getText().replace(",", ".").trim());
            Brand b = cbProductBrand.getSelectionModel().getSelectedItem();
            UnitOfMeasure u = cbProductUnit.getSelectionModel().getSelectedItem();
            Category mainC = cbMainCat.getSelectionModel().getSelectedItem();
            Category subC = cbSubCat.getSelectionModel().getSelectedItem();
            Integer catId = (mainC != null && mainC.getId() > 0) ? mainC.getId() : null;
            Integer subId = (subC != null && subC.getId() > 0) ? subC.getId() : null;

            if (currentEditingProduct == null) {
                Product np = new Product(0, txtProductName.getText(), productDAO.generateNewArticle(catId != null ? catId : 0), price, txtProductCharacteristic.getText(), discount, vat, b != null ? b.getId() : 0, u != null ? u.getId() : 0, catId, subId, imgBytes);
                if (productDAO.addProduct(np)) { loadProductsData(); updateStatus("Товар добавлен"); }
            } else {
                BigDecimal oldPrice = currentEditingProduct.getPrice();
                currentEditingProduct.setName(txtProductName.getText()); currentEditingProduct.setCharacteristic(txtProductCharacteristic.getText());
                currentEditingProduct.setPrice(price); currentEditingProduct.setDiscount(discount); currentEditingProduct.setVat(vat);
                currentEditingProduct.setBrandId(b != null ? b.getId() : 0); currentEditingProduct.setUnitId(u != null ? u.getId() : 0);
                currentEditingProduct.setCategoryId(catId); currentEditingProduct.setSubcategoryId(subId); currentEditingProduct.setImage(imgBytes);

                if (productDAO.updateProduct(currentEditingProduct)) {
                    if (oldPrice != null && price != null && oldPrice.compareTo(price) != 0) {
                        new PriceHistoryDAO().logPriceChange(currentEditingProduct.getId(), oldPrice, price);
                    }
                    tableProducts.refresh(); updateStatus("Товар обновлен");
                }
            }
        } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Ошибка", "Проверьте числовые поля!"); }
    }

    private void updateStatus(String msg) { if (statusCallback != null) statusCallback.accept(msg); }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait();
    }
}