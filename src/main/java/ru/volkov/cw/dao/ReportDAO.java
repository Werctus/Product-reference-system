package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Класс доступа к данным (DAO) для формирования сводных отчетов
public class ReportDAO {

    // Вызов функции БД get_delivery_report для сводного отчета
    // null для отключения фильтрации
    public List<String[]> getDeliveryReport(LocalDate startDate, LocalDate endDate,
                                            Integer storeId, Integer companyId, Integer categoryId) {

        List<String[]> reportData = new ArrayList<>();
        // SQL-запрос для вызова табличной функции
        String sql = "SELECT * FROM public.get_delivery_report(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров с проверкой на null
            if (startDate != null) pstmt.setDate(1, Date.valueOf(startDate));
            else pstmt.setNull(1, Types.DATE);

            if (endDate != null) pstmt.setDate(2, Date.valueOf(endDate));
            else pstmt.setNull(2, Types.DATE);

            if (storeId != null) pstmt.setInt(3, storeId);
            else pstmt.setNull(3, Types.INTEGER);

            if (companyId != null) pstmt.setInt(4, companyId);
            else pstmt.setNull(4, Types.INTEGER);

            if (categoryId != null) pstmt.setInt(5, categoryId);
            else pstmt.setNull(5, Types.INTEGER);

            // Выполнение запроса и сборка результатов
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[8];
                    row[0] = rs.getTimestamp("delivery_date").toString();
                    row[1] = rs.getString("document_number");
                    row[2] = rs.getString("company_name");
                    row[3] = rs.getString("store_name");
                    row[4] = rs.getString("product_name");
                    row[5] = rs.getString("category_name");
                    row[6] = String.valueOf(rs.getInt("quantity"));
                    row[7] = rs.getBigDecimal("total_cost").toString();
                    reportData.add(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportData;
    }

    // Вызов хранимой процедуры для проведения поставки.
    // Изменяет статус и обновляет инвентарь магазина.
    public void completeDeliveryProcedure(int deliveryId) throws SQLException {
        String sql = "CALL public.complete_delivery(?)";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, deliveryId);
            cstmt.execute();
        }
    }
}