// =====================================================
// Файл: PriceHistoryDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.PriceHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PriceHistoryDAO - Работа с историей цен")
class PriceHistoryDAOTest {

    private static PriceHistoryDAO priceHistoryDAO;

    @BeforeAll
    static void setUp() {
        priceHistoryDAO = new PriceHistoryDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Логирование изменения цены")
    void testLogPriceChange() {
        priceHistoryDAO.logPriceChange(
                1,
                new BigDecimal("100.00"),
                new BigDecimal("120.00")
        );

        List<PriceHistory> history = priceHistoryDAO.getPriceHistoryByProduct(1);
        assertNotNull(history, "История цен не должна быть null");

        if (!history.isEmpty()) {
            PriceHistory latest = history.get(history.size() - 1);
            System.out.println("Записано изменение цены:");
            System.out.println("  Продукт ID: " + latest.getProductId());
            System.out.println("  Старая цена: " + latest.getOldPrice());
            System.out.println("  Новая цена: " + latest.getNewPrice());
            System.out.println("  Дата изменения: " + latest.getChangedAt());
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Получение всей истории цен")
    void testGetAllPriceHistory() {
        List<PriceHistory> history = priceHistoryDAO.getPriceHistory();
        assertNotNull(history, "История цен не должна быть null");
        System.out.println("Всего записей в истории цен: " + history.size());
    }

    @Test
    @Order(3)
    @DisplayName("3. Фильтрация истории цен по продукту")
    void testGetPriceHistoryByProduct() {
        List<PriceHistory> history = priceHistoryDAO.getPriceHistoryByProduct(1);
        assertNotNull(history, "История цен по продукту не должна быть null");
        System.out.println("Записей для продукта ID=1: " + history.size());

        // Проверяем, что все записи относятся к продукту 1
        for (PriceHistory ph : history) {
            assertEquals(1, ph.getProductId(), "Все записи должны относиться к продукту 1");
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. Фильтрация истории цен по диапазону дат")
    void testGetPriceHistoryByDateRange() {
        LocalDateTime from = LocalDateTime.now().minusDays(30);
        LocalDateTime to = LocalDateTime.now();

        List<PriceHistory> history = priceHistoryDAO.getPriceHistoryByDateRange(from, to);
        assertNotNull(history, "История цен за период не должна быть null");
        System.out.println("Записей за последние 30 дней: " + history.size());

        // Проверяем, что все записи входят в диапазон
        for (PriceHistory ph : history) {
            assertTrue(
                    !ph.getChangedAt().isBefore(from) && !ph.getChangedAt().isAfter(to),
                    "Дата изменения должна быть в указанном диапазоне"
            );
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. Множественное логирование изменений")
    void testMultiplePriceChanges() {
        // Логируем несколько изменений цены
        BigDecimal[] prices = {
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                new BigDecimal("105.00"),
                new BigDecimal("115.00"),
                new BigDecimal("120.00")
        };

        for (int i = 0; i < prices.length - 1; i++) {
            priceHistoryDAO.logPriceChange(1, prices[i], prices[i + 1]);
        }

        List<PriceHistory> history = priceHistoryDAO.getPriceHistoryByProduct(1);
        System.out.println("После множественного логирования записей: " + history.size());
    }

    @Test
    @Order(6)
    @DisplayName("6. Проверка структуры записи истории цен")
    void testPriceHistoryStructure() {
        List<PriceHistory> history = priceHistoryDAO.getPriceHistory();

        if (history.isEmpty()) {
            System.out.println("Нет записей истории цен для проверки структуры");
            return;
        }

        PriceHistory ph = history.get(0);

        assertNotNull(ph.getProductId(), "ID продукта не должен быть null");
        assertNotNull(ph.getOldPrice(), "Старая цена не должна быть null");
        assertNotNull(ph.getNewPrice(), "Новая цена не должна быть null");
        assertNotNull(ph.getChangedAt(), "Дата изменения не должна быть null");

        System.out.println("Структура записи истории цен:");
        System.out.println("  Продукт ID: " + ph.getProductId());
        System.out.println("  Продукт: " + ph.getProductName());
        System.out.println("  Старая цена: " + ph.getOldPrice());
        System.out.println("  Новая цена: " + ph.getNewPrice());
        System.out.println("  Дата: " + ph.getChangedAt());
    }
}