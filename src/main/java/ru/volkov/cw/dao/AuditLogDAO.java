package ru.volkov.cw.dao;

import ru.volkov.cw.model.AuditLog;
import ru.volkov.cw.DatabaseConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class AuditLogDAO {

    public ObservableList<AuditLog> getAllAuditLogs() {
        ObservableList<AuditLog> list = FXCollections.observableArrayList();
        String sql = """
            SELECT id, table_name, record_id, action, old_data, new_data, user_name, timestamp
            FROM audit_log
            ORDER BY timestamp DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getInt("record_id"));
                log.setAction(rs.getString("action"));
                log.setOldData(rs.getString("old_data"));
                log.setNewData(rs.getString("new_data"));
                log.setUserName(rs.getString("user_name"));
                log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                list.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<AuditLog> getAuditLogsByTable(String tableName) {
        ObservableList<AuditLog> list = FXCollections.observableArrayList();
        String sql = """
            SELECT id, table_name, record_id, action, old_data, new_data, user_name, timestamp
            FROM audit_log
            WHERE table_name = ?
            ORDER BY timestamp DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getInt("record_id"));
                log.setAction(rs.getString("action"));
                log.setOldData(rs.getString("old_data"));
                log.setNewData(rs.getString("new_data"));
                log.setUserName(rs.getString("user_name"));
                log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                list.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<AuditLog> getAuditLogsByAction(String action) {
        ObservableList<AuditLog> list = FXCollections.observableArrayList();
        String sql = """
            SELECT id, table_name, record_id, action, old_data, new_data, user_name, timestamp
            FROM audit_log
            WHERE action = ?
            ORDER BY timestamp DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, action);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getInt("record_id"));
                log.setAction(rs.getString("action"));
                log.setOldData(rs.getString("old_data"));
                log.setNewData(rs.getString("new_data"));
                log.setUserName(rs.getString("user_name"));
                log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                list.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<AuditLog> getAuditLogsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        ObservableList<AuditLog> list = FXCollections.observableArrayList();
        String sql = """
            SELECT id, table_name, record_id, action, old_data, new_data, user_name, timestamp
            FROM audit_log
            WHERE timestamp BETWEEN ? AND ?
            ORDER BY timestamp DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(fromDate));
            stmt.setTimestamp(2, Timestamp.valueOf(toDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getInt("record_id"));
                log.setAction(rs.getString("action"));
                log.setOldData(rs.getString("old_data"));
                log.setNewData(rs.getString("new_data"));
                log.setUserName(rs.getString("user_name"));
                log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                list.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<AuditLog> getAuditLogsByFilters(String tableName, String action,
                                                          LocalDateTime fromDate, LocalDateTime toDate) {
        ObservableList<AuditLog> list = FXCollections.observableArrayList();

        StringBuilder sql = new StringBuilder("""
            SELECT id, table_name, record_id, action, old_data, new_data, user_name, timestamp
            FROM audit_log
            WHERE 1=1
            """);

        if (tableName != null && !tableName.isEmpty()) {
            sql.append(" AND table_name = ?");
        }
        if (action != null && !action.isEmpty()) {
            sql.append(" AND action = ?");
        }
        if (fromDate != null) {
            sql.append(" AND timestamp >= ?");
        }
        if (toDate != null) {
            sql.append(" AND timestamp <= ?");
        }

        sql.append(" ORDER BY timestamp DESC");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (tableName != null && !tableName.isEmpty()) {
                stmt.setString(paramIndex++, tableName);
            }
            if (action != null && !action.isEmpty()) {
                stmt.setString(paramIndex++, action);
            }
            if (fromDate != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(fromDate));
            }
            if (toDate != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(toDate));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getInt("record_id"));
                log.setAction(rs.getString("action"));
                log.setOldData(rs.getString("old_data"));
                log.setNewData(rs.getString("new_data"));
                log.setUserName(rs.getString("user_name"));
                log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                list.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteOldAuditLogs(int daysToKeep) {
        String sql = """
            DELETE FROM audit_log
            WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '? days'
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, daysToKeep);
            int affectedRows = stmt.executeUpdate();
            System.out.println("Удалено старых записей аудита: " + affectedRows);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearAllAuditLogs() {
        String sql = "DELETE FROM audit_log";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int affectedRows = stmt.executeUpdate();
            System.out.println("Очищено записей аудита: " + affectedRows);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}