    package ru.volkov.cw.controller;

    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.util.StringConverter;
    import ru.volkov.cw.dao.PriceHistoryDAO;
    import ru.volkov.cw.dao.ProductDAO;
    import ru.volkov.cw.model.PriceHistory;
    import ru.volkov.cw.model.Product;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.function.Consumer;

    public class PriceHistoryController {
        @FXML private ComboBox<Product> cbPriceHistoryProduct;
        @FXML private DatePicker dpPriceHistoryFrom, dpPriceHistoryTo;
        @FXML private Button btnPriceHistoryFilter, btnPriceHistoryReset;
        @FXML private TableView<PriceHistory> tablePriceHistory;
        @FXML private TableColumn<PriceHistory, String> colPriceProduct;
        @FXML private TableColumn<PriceHistory, BigDecimal> colPriceOld, colPriceNew;
        @FXML private TableColumn<PriceHistory, LocalDateTime> colPriceDate;

        private final PriceHistoryDAO priceHistoryDAO = new PriceHistoryDAO();
        private final ProductDAO productDAO = new ProductDAO();
        private ObservableList<PriceHistory> priceHistoryData = FXCollections.observableArrayList();
        private Consumer<String> statusCallback;

        public void setStatusCallback(Consumer<String> statusCallback) { this.statusCallback = statusCallback; }

        @FXML public void initialize() {
            colPriceProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
            colPriceOld.setCellValueFactory(new PropertyValueFactory<>("oldPrice"));
            colPriceNew.setCellValueFactory(new PropertyValueFactory<>("newPrice"));
            colPriceDate.setCellValueFactory(new PropertyValueFactory<>("changedAt"));
            colPriceDate.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(LocalDateTime d, boolean empty) { super.updateItem(d, empty); setText(empty || d == null ? null : d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))); } });

            cbPriceHistoryProduct.getItems().add(null); cbPriceHistoryProduct.getItems().addAll(productDAO.getAllProducts());
            cbPriceHistoryProduct.setConverter(new StringConverter<>() { @Override public String toString(Product p) { return p == null ? "Все товары" : p.getName(); } @Override public Product fromString(String s) { return null; } });

            btnPriceHistoryFilter.setOnAction(e -> filterPriceHistory());
            btnPriceHistoryReset.setOnAction(e -> { cbPriceHistoryProduct.getSelectionModel().select(0); dpPriceHistoryFrom.setValue(null); dpPriceHistoryTo.setValue(null); loadPriceHistory(); });
            loadPriceHistory();
        }

        private void loadPriceHistory() { priceHistoryData.clear(); priceHistoryData.addAll(priceHistoryDAO.getPriceHistory()); tablePriceHistory.setItems(priceHistoryData); }

        private void filterPriceHistory() {
            Product sel = cbPriceHistoryProduct.getValue();
            priceHistoryData.clear();
            if (sel != null) priceHistoryData.addAll(priceHistoryDAO.getPriceHistoryByProduct(sel.getId()));
            else if (dpPriceHistoryFrom.getValue() != null && dpPriceHistoryTo.getValue() != null) {
                priceHistoryData.addAll(priceHistoryDAO.getPriceHistoryByDateRange(dpPriceHistoryFrom.getValue().atStartOfDay(), dpPriceHistoryTo.getValue().atTime(23, 59, 59)));
            } else loadPriceHistory();
        }
    }