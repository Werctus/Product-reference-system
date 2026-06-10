package ru.volkov.cw.dao;

import ru.volkov.cw.DatabaseConfig;
import ru.volkov.cw.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс доступа к данным для сущности "Фирма".
 * Управляет записями в таблице company.
 */
public class CompanyDAO {

    /**
     * Получает список всех фирм из базы данных.
     *
     * @return список объектов Company
     */
    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM public.company ORDER BY id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Company company = new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("inn"),
                        rs.getBigDecimal("rating"),
                        rs.getString("phone"),
                        rs.getString("address")
                );
                companies.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies;
    }

    /**
     * Добавляет новую фирму в базу данных.
     *
     * @param company объект фирмы для добавления
     * @return true, если вставка прошла успешно
     */
    public boolean addCompany(Company company) {
        String sql = "INSERT INTO public.company (name, inn, rating, phone, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getInn());
            pstmt.setBigDecimal(3, company.getRating());
            pstmt.setString(4, company.getPhone());
            pstmt.setString(5, company.getAddress());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Обновляет информацию о существующей фирме.
     *
     * @param company объект фирмы с обновленными данными
     * @return true, если обновление прошло успешно
     */
    public boolean updateCompany(Company company) {
        String sql = "UPDATE public.company SET name = ?, inn = ?, rating = ?, phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getInn());
            pstmt.setBigDecimal(3, company.getRating());
            pstmt.setString(4, company.getPhone());
            pstmt.setString(5, company.getAddress());
            pstmt.setInt(6, company.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет фирму из базы данных.
     *
     * @param id идентификатор удаляемой фирмы
     * @return true, если удаление прошло успешно
     */
    public boolean deleteCompany(int id) {
        String sql = "DELETE FROM public.company WHERE id = ?";

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