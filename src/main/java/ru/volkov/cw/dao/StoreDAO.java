package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import ru.volkov.cw.model.Store;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreDAO {

    public List<Store> getAllStores() {
        List<Store> stores = new ArrayList<>();
        String sql = "SELECT * FROM public.store ORDER BY id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Store store = new Store(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("email")
                );
                stores.add(store);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stores;
    }

    public boolean addStore(Store store) {
        String sql = "INSERT INTO public.store (name, phone, address, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, store.getName());
            pstmt.setString(2, store.getPhone());
            pstmt.setString(3, store.getAddress());
            pstmt.setString(4, store.getEmail());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStore(Store store) {
        String sql = "UPDATE public.store SET name = ?, phone = ?, address = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, store.getName());
            pstmt.setString(2, store.getPhone());
            pstmt.setString(3, store.getAddress());
            pstmt.setString(4, store.getEmail());
            pstmt.setInt(5, store.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStore(int id) {
        String sql = "DELETE FROM public.store WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}