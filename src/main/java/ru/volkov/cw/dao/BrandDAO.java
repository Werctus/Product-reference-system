package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import ru.volkov.cw.model.Brand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс доступа к данным для таблицы справочника брендов (brand).
 */
public class BrandDAO {

    /**
     * Извлекает все записи о брендах из базы данных.
     * Сортировка выполняется по идентификатору по возрастанию.
     *
     * @return список объектов Brand
     */
    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT * FROM public.brand ORDER BY id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Brand brand = new Brand(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                brands.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brands;
    }

    /**
     * Выполняет вставку нового бренда в таблицу.
     *
     * @param brand объект бренда для сохранения
     * @return true, если вставка завершилась успешно
     */
    public boolean addBrand(Brand brand) {
        String sql = "INSERT INTO public.brand (name) VALUES (?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, brand.getName());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Обновляет текстовое название существующего бренда по его ID.
     *
     * @param brand объект бренда с новыми данными
     * @return true, если обновление завершилось успешно
     */
    public boolean updateBrand(Brand brand) {
        String sql = "UPDATE public.brand SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, brand.getName());
            pstmt.setInt(2, brand.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет запись о бренде по указанному идентификатору.
     * Внимание: при наличии связанных товаров удаление может быть заблокировано или выполнено каскадно (зависит от внешнего ключа).
     *
     * @param id идентификатор бренда
     * @return true, если удаление завершилось успешно
     */
    public boolean deleteBrand(int id) {
        String sql = "DELETE FROM public.brand WHERE id = ?";

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