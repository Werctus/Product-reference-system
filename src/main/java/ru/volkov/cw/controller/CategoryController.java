package ru.volkov.cw.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import ru.volkov.cw.dao.CategoryDAO;
import ru.volkov.cw.model.Category;
import java.util.List;
import java.util.function.Consumer;

public class CategoryController {
    @FXML private TreeView<Category> treeCategories;
    @FXML private TextField txtCategoryName;
    @FXML private ComboBox<Category> cbParentCategory;
    @FXML private Button btnDeleteCategory, btnSaveCategory;

    private final CategoryDAO catDao = new CategoryDAO();
    private Category currentEditingCategory = null;
    private Consumer<String> statusCallback;
    private Runnable onDataChanged;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }
    public void setOnDataChanged(Runnable onDataChanged) { this.onDataChanged = onDataChanged; }

    @FXML public void initialize() {
        treeCategories.setCellFactory(tv -> new TreeCell<>() {
            @Override protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(item.getName());
                    setStyle((item.getParentId() == null || item.getParentId() == 0) ? "-fx-font-weight: bold;" : "-fx-font-weight: normal;");
                }
            }
        });
        cbParentCategory.setConverter(new StringConverter<>() { @Override public String toString(Category c) { return c == null ? "" : c.getName(); } @Override public Category fromString(String s) { return null; } });
        treeCategories.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null && n.getValue() != null && n.getValue().getId() > 0) {
                currentEditingCategory = n.getValue();
                txtCategoryName.setText(n.getValue().getName());
            }
        });
        btnDeleteCategory.setOnAction(e -> {
            TreeItem<Category> sel = treeCategories.getSelectionModel().getSelectedItem();
            if (sel != null && sel.getValue().getId() > 0) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && catDao.deleteCategory(sel.getValue().getId())) { refresh(); notifyChanged(); } });
            }
        });
        btnSaveCategory.setOnAction(e -> {
            String name = txtCategoryName.getText().trim();
            if (!name.isEmpty()) {
                Category parent = cbParentCategory.getSelectionModel().getSelectedItem();
                Integer pid = (parent != null && parent.getId() > 0) ? parent.getId() : null;
                if (currentEditingCategory == null) {
                    if (catDao.addCategory(new Category(name, pid))) { refresh(); notifyChanged(); }
                } else {
                    currentEditingCategory.setName(name); currentEditingCategory.setParentId(pid);
                    if (catDao.updateCategory(currentEditingCategory)) { refresh(); notifyChanged(); currentEditingCategory = null; }
                }
                txtCategoryName.clear();
            }
        });
        refresh();
    }

    private void refresh() {
        TreeItem<Category> root = new TreeItem<>(new Category(0, "Корень", null)); root.setExpanded(true);
        List<Category> all = catDao.getAllCategories();
        for (Category c : all) {
            if (c.getParentId() == null || c.getParentId() == 0) {
                TreeItem<Category> main = new TreeItem<>(c); main.setExpanded(true);
                for (Category sub : all) if (sub.getParentId() != null && sub.getParentId().equals(c.getId())) main.getChildren().add(new TreeItem<>(sub));
                root.getChildren().add(main);
            }
        }
        treeCategories.setRoot(root); treeCategories.setShowRoot(false);

        cbParentCategory.getItems().clear();
        Category noParent = new Category(0, "--- Нет ---", null);
        cbParentCategory.getItems().add(noParent);
        cbParentCategory.getItems().addAll(catDao.getMainCategories());
    }

    private void notifyChanged() { if (onDataChanged != null) onDataChanged.run(); }
}