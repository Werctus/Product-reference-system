package ru.volkov.cw.dao;

import ru.volkov.cw.model.PriceHistory;
import ru.volkov.cw.DatabaseConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class PriceHistoryDAO {

    public boolean addPriceHistory(PriceHistory history) {
        String sql = """
            INSERT INTO price_history (product_id, old_price, new_price, changed_at)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, history.getProductId());
            stmt.setBigDecimal(2, history.getOldPrice());
            stmt.setBigDecimal(3, history.getNewPrice());
            stmt.setTimestamp(4, Timestamp.valueOf(history.getChangedAt()));

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<PriceHistory> getPriceHistory() {
        ObservableList<PriceHistory> list = FXCollections.observableArrayList();
        String sql = """
            SELECT ph.id, ph.product_id, p.name as product_name, 
                   ph.old_price, ph.new_price, ph.changed_at
            FROM price_history ph
            JOIN product p ON ph.product_id = p.id
            ORDER BY ph.changed_at DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PriceHistory history = new PriceHistory();
                history.setId(rs.getInt("id"));
                history.setProductId(rs.getInt("product_id"));
                history.setProductName(rs.getString("product_name"));
                history.setOldPrice(rs.getBigDecimal("old_price"));
                history.setNewPrice(rs.getBigDecimal("new_price"));
                history.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime());
                list.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<PriceHistory> getPriceHistoryByProduct(int productId) {
        ObservableList<PriceHistory> list = FXCollections.observableArrayList();
        String sql = """
            SELECT ph.id, ph.product_id, p.name as product_name, 
                   ph.old_price, ph.new_price, ph.changed_at
            FROM price_history ph
            JOIN product p ON ph.product_id = p.id
            WHERE ph.product_id = ?
            ORDER BY ph.changed_at DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PriceHistory history = new PriceHistory();
                history.setId(rs.getInt("id"));
                history.setProductId(rs.getInt("product_id"));
                history.setProductName(rs.getString("product_name"));
                history.setOldPrice(rs.getBigDecimal("old_price"));
                history.setNewPrice(rs.getBigDecimal("new_price"));
                history.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime());
                list.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<PriceHistory> getPriceHistoryByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        ObservableList<PriceHistory> list = FXCollections.observableArrayList();
        String sql = """
            SELECT ph.id, ph.product_id, p.name as product_name, 
                   ph.old_price, ph.new_price, ph.changed_at
            FROM price_history ph
            JOIN product p ON ph.product_id = p.id
            WHERE ph.changed_at BETWEEN ? AND ?
            ORDER BY ph.changed_at DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(fromDate));
            stmt.setTimestamp(2, Timestamp.valueOf(toDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PriceHistory history = new PriceHistory();
                history.setId(rs.getInt("id"));
                history.setProductId(rs.getInt("product_id"));
                history.setProductName(rs.getString("product_name"));
                history.setOldPrice(rs.getBigDecimal("old_price"));
                history.setNewPrice(rs.getBigDecimal("new_price"));
                history.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime());
                list.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void logPriceChange(int productId, BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice != null && newPrice != null && oldPrice.compareTo(newPrice) != 0) {
            PriceHistory history = new PriceHistory();
            history.setProductId(productId);
            history.setOldPrice(oldPrice);
            history.setNewPrice(newPrice);
            history.setChangedAt(LocalDateTime.now());

            boolean success = addPriceHistory(history);
            System.out.println("Запись в историю цен: " + (success ? "УСПЕШНО" : "НЕ УДАЛОСЬ"));
        }
    }
}