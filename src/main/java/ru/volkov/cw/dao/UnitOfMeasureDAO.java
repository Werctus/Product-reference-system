package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import ru.volkov.cw.model.UnitOfMeasure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс доступа к данным для таблицы справочника единиц измерения (unit_of_measure).
 */
public class UnitOfMeasureDAO {

    /**
     * Извлекает все единицы измерения из базы данных.
     *
     * @return список объектов UnitOfMeasure
     */
    public List<UnitOfMeasure> getAllUnits() {
        List<UnitOfMeasure> units = new ArrayList<>();
        String sql = "SELECT * FROM public.unit_of_measure ORDER BY id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UnitOfMeasure unit = new UnitOfMeasure(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                units.add(unit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return units;
    }

    /**
     * Выполняет вставку новой единицы измерения в таблицу.
     *
     * @param unit объект единицы измерения для сохранения
     * @return true, если вставка завершилась успешно
     */
    public boolean addUnit(UnitOfMeasure unit) {
        String sql = "INSERT INTO public.unit_of_measure (name) VALUES (?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, unit.getName());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Обновляет название существующей единицы измерения по её ID.
     *
     * @param unit объект единицы измерения с новыми данными
     * @return true, если обновление завершилось успешно
     */
    public boolean updateUnit(UnitOfMeasure unit) {
        String sql = "UPDATE public.unit_of_measure SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, unit.getName());
            pstmt.setInt(2, unit.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет единицу измерения по указанному идентификатору.
     *
     * @param id идентификатор единицы измерения
     * @return true, если удаление завершилось успешно
     */
    public boolean deleteUnit(int id) {
        String sql = "DELETE FROM public.unit_of_measure WHERE id = ?";

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