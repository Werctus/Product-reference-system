package ru.volkov.cw.dao;

import ru.volkov.cw.model.DeliveryItem;
import ru.volkov.cw.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryItemDAO {

    public List<DeliveryItem> getItemsByDelivery(int deliveryId) {
        List<DeliveryItem> list = new ArrayList<>();
        String sql = "SELECT di.delivery_id, di.product_id, di.quantity, di.price_per_unit, di.total, p.name as product_name " +
                "FROM delivery_item di " +
                "JOIN product p ON di.product_id = p.id " +
                "WHERE di.delivery_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DeliveryItem item = new DeliveryItem();
                item.setDeliveryId(rs.getInt("delivery_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price_per_unit"));
                item.setProductName(rs.getString("product_name"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addItem(DeliveryItem item) {
        String sql = "INSERT INTO delivery_item (delivery_id, product_id, quantity, price_per_unit, total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getDeliveryId());
            pstmt.setInt(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setBigDecimal(4, item.getPrice());

            BigDecimal total = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            pstmt.setBigDecimal(5, total);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItem(DeliveryItem item) {
        String sql = "UPDATE delivery_item SET quantity = ?, price_per_unit = ?, total = ? WHERE delivery_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getQuantity());
            pstmt.setBigDecimal(2, item.getPrice());

            BigDecimal total = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            pstmt.setBigDecimal(3, total);

            pstmt.setInt(4, item.getDeliveryId());
            pstmt.setInt(5, item.getProductId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(int deliveryId, int productId) {
        String sql = "DELETE FROM delivery_item WHERE delivery_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);
            pstmt.setInt(2, productId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}