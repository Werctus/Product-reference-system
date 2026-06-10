package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import ru.volkov.cw.model.Delivery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс доступа к данным для работы с документами поставок.
 * Управляет записями в таблице delivery.
 */
public class DeliveryDAO {

    /**
     * Получает список всех документов поставок.
     *
     * @return список объектов Delivery
     */
    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM public.delivery ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Delivery delivery = new Delivery(
                        rs.getInt("id"),
                        rs.getString("document_number"),
                        rs.getString("status"),
                        rs.getInt("company_id"),
                        rs.getInt("store_id"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                deliveries.add(delivery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveries;
    }



    public int addDelivery(Delivery del) {
        // Убираем document_number из списка полей. БД сгенерирует его сама.
        // Используем RETURNING id, чтобы получить ID созданной записи
        String sql = "INSERT INTO public.delivery (company_id, store_id, status) VALUES (?, ?, 'pending') RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, del.getCompanyId());
            pstmt.setInt(2, del.getStoreId());

            // Выполняем запрос и забираем сгенерированный ID
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Удаляет документ поставки.
     * Каскадное удаление (ON DELETE CASCADE) автоматически удалит все связанные позиции из delivery_item.
     *
     * @param id идентификатор удаляемой поставки
     * @return true, если удаление прошло успешно
     */
    public boolean deleteDelivery(int id) {
        String sql = "DELETE FROM public.delivery WHERE id = ?";

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