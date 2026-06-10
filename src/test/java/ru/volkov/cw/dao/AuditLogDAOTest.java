// =====================================================
// Файл: AuditLogDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AuditLogDAO - Работа с журналом аудита")
class AuditLogDAOTest {

    private static AuditLogDAO auditLogDAO;

    @BeforeAll
    static void setUp() {
        auditLogDAO = new AuditLogDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех записей аудита")
    void testGetAllAuditLogs() {
        List<AuditLog> logs = auditLogDAO.getAllAuditLogs();
        assertNotNull(logs, "Список записей аудита не должен быть null");
        System.out.println("Количество записей аудита: " + logs.size());

        if (!logs.isEmpty()) {
            AuditLog first = logs.get(0);
            System.out.println("Первая запись: Таблица=" + first.getTableName() +
                    ", Действие=" + first.getAction() +
                    ", Пользователь=" + first.getUserName());
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Фильтрация по таблице и действию")
    void testFilterByTableAndAction() {
        List<AuditLog> logs = auditLogDAO.getAuditLogsByFilters(
                "product", "INSERT", null, null
        );
        assertNotNull(logs, "Отфильтрованный список не должен быть null");
        System.out.println("Найдено записей (product + INSERT): " + logs.size());

        // Проверяем, что все записи соответствуют фильтру
        for (AuditLog log : logs) {
            assertEquals("product", log.getTableName(), "Таблица должна быть product");
            assertEquals("INSERT", log.getAction(), "Действие должно быть INSERT");
        }
    }

    @Test
    @Order(3)
    @DisplayName("3. Фильтрация по всем таблицам")
    void testFilterAllTables() {
        // Фильтр с null должен вернуть все записи
        List<AuditLog> allLogs = auditLogDAO.getAuditLogsByFilters(
                null, null, null, null
        );
        assertNotNull(allLogs, "Список всех записей не должен быть null");

        List<AuditLog> logs = auditLogDAO.getAllAuditLogs();
        assertEquals(logs.size(), allLogs.size(), "Количество записей должно совпадать");
        System.out.println("Фильтр без параметров вернул " + allLogs.size() + " записей");
    }

    @Test
    @Order(4)
    @DisplayName("4. Фильтрация по диапазону дат")
    void testFilterByDateRange() {
        LocalDateTime from = LocalDateTime.now().minusDays(30);
        LocalDateTime to = LocalDateTime.now();

        List<AuditLog> logs = auditLogDAO.getAuditLogsByFilters(
                null, null, from, to
        );
        assertNotNull(logs, "Отфильтрованный по датам список не должен быть null");
        System.out.println("Найдено записей за последние 30 дней: " + logs.size());
    }

    @Test
    @Order(5)
    @DisplayName("5. Фильтрация по нескольким параметрам")
    void testFilterByMultipleParams() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();

        List<AuditLog> logs = auditLogDAO.getAuditLogsByFilters(
                "product", "UPDATE", from, to
        );
        assertNotNull(logs, "Комбинированный фильтр не должен вернуть null");
        System.out.println("Найдено записей (product + UPDATE + 7 дней): " + logs.size());

        for (AuditLog log : logs) {
            System.out.println("  - " + log.getTimestamp() + " | " + log.getTableName() +
                    " | " + log.getAction() + " | " + log.getUserName());
        }
    }

    @Test
    @Order(6)
    @DisplayName("6. Проверка структуры записи аудита")
    void testAuditLogStructure() {
        List<AuditLog> logs = auditLogDAO.getAllAuditLogs();

        if (logs.isEmpty()) {
            System.out.println("Нет записей аудита для проверки структуры");
            return;
        }

        AuditLog log = logs.get(0);

        assertNotNull(log.getTableName(), "Имя таблицы не должно быть null");
        assertNotNull(log.getAction(), "Действие не должно быть null");
        assertNotNull(log.getTimestamp(), "Временная метка не должна быть null");

        System.out.println("Структура записи аудита:");
        System.out.println("  Таблица: " + log.getTableName());
        System.out.println("  Действие: " + log.getAction());
        System.out.println("  Пользователь: " + log.getUserName());
        System.out.println("  Время: " + log.getTimestamp());
        System.out.println("  Старые данные: " + (log.getOldData() != null ? log.getOldData().length() : 0) + " символов");
        System.out.println("  Новые данные: " + (log.getNewData() != null ? log.getNewData().length() : 0) + " символов");
    }
}