package ru.volkov.cw.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.volkov.cw.dao.CompanyDAO;
import ru.volkov.cw.model.Company;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class CompanyController {
    @FXML private TableView<Company> tableCompanies;
    @FXML private TableColumn<Company, String> colCompanyName, colCompanyINN, colCompanyNumber, colCompanyAddress;
    @FXML private TableColumn<Company, BigDecimal> colCompanyRating;
    @FXML private Button btnAddCompany, btnEditCompany, btnDeleteCompany;

    private final CompanyDAO companyDAO = new CompanyDAO();
    private ObservableList<Company> companyData = FXCollections.observableArrayList();
    private Consumer<String> statusCallback;

    public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

    @FXML public void initialize() { initCompanyTab(); }

    private void initCompanyTab() {
        colCompanyName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCompanyINN.setCellValueFactory(new PropertyValueFactory<>("inn"));
        colCompanyRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colCompanyNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colCompanyAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        loadCompaniesData();

        btnAddCompany.setOnAction(event -> showCompanyDialog(null).ifPresent(c -> {
            if (companyDAO.addCompany(c)) { loadCompaniesData(); updateStatus("Фирма добавлена: " + c.getName()); }
            else updateStatus("Ошибка добавления фирмы.");
        }));

        btnEditCompany.setOnAction(event -> {
            Company sel = tableCompanies.getSelectionModel().getSelectedItem();
            if (sel != null) showCompanyDialog(sel).ifPresent(c -> {
                if (companyDAO.updateCompany(c)) { tableCompanies.refresh(); updateStatus("Данные фирмы обновлены."); }
            }); else showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите фирму!");
        });

        btnDeleteCompany.setOnAction(event -> {
            Company sel = tableCompanies.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Удалить фирму " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
                conf.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.YES && companyDAO.deleteCompany(sel.getId())) {
                        companyData.remove(sel); updateStatus("Фирма удалена.");
                    }
                });
            } else showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите фирму!");
        });
    }

    private void loadCompaniesData() {
        companyData.clear(); companyData.addAll(companyDAO.getAllCompanies()); tableCompanies.setItems(companyData);
    }

    private Optional<Company> showCompanyDialog(Company companyToEdit) {
        Dialog<Company> dialog = new Dialog<>();
        dialog.setTitle(companyToEdit == null ? "Добавить фирму" : "Редактировать фирму");
        dialog.setHeaderText("Заполните данные о фирме");
        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Название");
        TextField addressField = new TextField(); addressField.setPromptText("Адрес");
        TextField ratingField = new TextField(); ratingField.setPromptText("0.0 - 5.0");
        ratingField.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,1}([.,]\\d{0,2})?") ? change : null));

        TextField innField = new TextField(); innField.setPromptText("10 или 12 цифр");
        innField.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));

        TextField phoneField = new TextField(); phoneField.setPromptText("9991234567");
        phoneField.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,10}") ? change : null));
        javafx.scene.layout.HBox phoneBox = new javafx.scene.layout.HBox(5, new Label("+7"), phoneField);
        phoneBox.setAlignment(Pos.CENTER_LEFT);

        if (companyToEdit != null) {
            nameField.setText(companyToEdit.getName()); innField.setText(companyToEdit.getInn());
            ratingField.setText(companyToEdit.getRating() != null ? companyToEdit.getRating().toString() : "0.00");
            addressField.setText(companyToEdit.getAddress());
            String p = companyToEdit.getPhone(); phoneField.setText(p != null && p.startsWith("+7") ? p.substring(2) : p);
        }

        grid.add(new Label("Название:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("ИНН:"), 0, 1); grid.add(innField, 1, 1);
        grid.add(new Label("Рейтинг:"), 0, 2); grid.add(ratingField, 1, 2);
        grid.add(new Label("Телефон:"), 0, 3); grid.add(phoneBox, 1, 3);
        grid.add(new Label("Адрес:"), 0, 4); grid.add(addressField, 1, 4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    BigDecimal r = ratingField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(ratingField.getText().replace(",", "."));
                    return companyToEdit == null ?
                            new Company(nameField.getText(), innField.getText(), r, "+7" + phoneField.getText(), addressField.getText()) :
                            new Company(companyToEdit.getId(), nameField.getText(), innField.getText(), r, "+7" + phoneField.getText(), addressField.getText());
                } catch (Exception e) { return null; }
            }
            return null;
        });
        return dialog.showAndWait();
    }

    private void updateStatus(String msg) { if (statusCallback != null) statusCallback.accept(msg); }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait();
    }
}