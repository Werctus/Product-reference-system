package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import ru.volkov.cw.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс доступа к данным для таблицы категорий (category).
 */
public class CategoryDAO {

    /**
     * Получает абсолютно все категории (и главные, и подкатегории).
     */
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM public.category ORDER BY id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));

                // Обработка parent_id, так как он может быть NULL
                int pId = rs.getInt("parent_id");
                if (rs.wasNull()) {
                    c.setParentId(null);
                } else {
                    c.setParentId(pId);
                }

                c.setPrefix(rs.getString("prefix"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Получает только главные категории (у которых нет родителя).
     */
    public List<Category> getMainCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM public.category WHERE parent_id IS NULL ORDER BY name ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setParentId(null);
                c.setPrefix(rs.getString("prefix"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Получает подкатегории для конкретной главной категории.
     */
    public List<Category> getSubCategories(int parentId) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM public.category WHERE parent_id = ? ORDER BY name ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category c = new Category();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setParentId(rs.getInt("parent_id"));
                    c.setPrefix(rs.getString("prefix"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Добавляет новую категорию и автоматически генерирует префикс (3 буквы).
     */
    public boolean addCategory(Category c) {
        String sql = "INSERT INTO public.category (name, parent_id, prefix) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getName());

            // Если есть родитель - ставим его ID, если нет - ставим NULL
            if (c.getParentId() != null && c.getParentId() > 0) {
                pstmt.setInt(2, c.getParentId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            // Автоматически берем первые 3 буквы для префикса артикула
            String pref = "XXX";
            if (c.getName() != null && c.getName().length() >= 3) {
                pref = c.getName().substring(0, 3).toUpperCase();
            } else if (c.getName() != null) {
                pref = c.getName().toUpperCase();
            }
            pstmt.setString(3, pref);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Обновляет существующую категорию.
     */
    public boolean updateCategory(Category c) {
        String sql = "UPDATE public.category SET name = ?, parent_id = ?, prefix = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getName());

            if (c.getParentId() != null && c.getParentId() > 0) {
                pstmt.setInt(2, c.getParentId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, c.getPrefix());
            pstmt.setInt(4, c.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет категорию по ID.
     */
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM public.category WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}