package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import ru.volkov.cw.model.InventoryItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    // Получаем инвентарь для конкретного магазина
    public List<InventoryItem> getItems(int storeId) {
        List<InventoryItem> list = new ArrayList<>();
        // Используем JOIN для получения названия товара из таблицы product
        String sql = "SELECT i.product_id, p.name, i.quantity, i.min_stock, p.characteristic " +
                "FROM public.store_product_inventory i " +
                "JOIN public.product p ON i.product_id = p.id " +
                "WHERE i.store_id = ?";

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, storeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new InventoryItem(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getInt("min_stock"),
                        rs.getString("characteristic")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Добавляем новый товар в магазин (если его там еще нет)
    public boolean addProductToStore(int storeId, int productId, int qty, int min) {
        String sql = "INSERT INTO public.store_product_inventory (store_id, product_id, quantity, min_stock) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, storeId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.setInt(4, min);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновляем количество и минимальный запас
    public boolean updateInventory(int storeId, int productId, int qty, int min) {
        String sql = "UPDATE public.store_product_inventory SET quantity = ?, min_limit = ?, WHERE store_id = ? AND product_id = ?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setInt(2, min);
            ps.setInt(3, storeId);
            ps.setInt(4, productId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}