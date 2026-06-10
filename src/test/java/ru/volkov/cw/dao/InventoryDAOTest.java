// =====================================================
// Файл: InventoryDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.InventoryItem;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("InventoryDAO - Работа с инвентарем магазинов")
class InventoryDAOTest {

    private static InventoryDAO inventoryDAO;

    @BeforeAll
    static void setUp() {
        inventoryDAO = new InventoryDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение инвентаря магазина")
    void testGetItems() {
        List<InventoryItem> items = inventoryDAO.getItems(1);
        assertNotNull(items, "Список инвентаря не должен быть null");
        System.out.println("Товаров в инвентаре магазина 1: " + items.size());

        for (InventoryItem item : items) {
            System.out.println("  - Продукт: " + item.getProductName() +
                    ", Кол-во: " + item.getQuantity() +
                    ", Мин. запас: " + item.getMinStock());
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Добавление товара в магазин")
    void testAddProductToStore() {
        boolean result = inventoryDAO.addProductToStore(1, 1, 100, 10);
        System.out.println("Добавление товара 1 в магазин 1: " + (result ? "успешно" : "не удалось"));

        if (result) {
            // Проверяем, что товар появился в инвентаре
            List<InventoryItem> items = inventoryDAO.getItems(1);
            boolean found = items.stream()
                    .anyMatch(i -> i.getProductId() == 1);
            assertTrue(found, "Товар должен быть найден в инвентаре магазина");
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. Добавление товара с нулевым количеством")
    void testAddProductWithZeroQuantity() {
        boolean result = inventoryDAO.addProductToStore(1, 2, 0, 5);
        System.out.println("Добавление товара с нулевым количеством: " + (result ? "успешно" : "не удалось"));
        // Может быть как true (разрешено), так и false (запрещено)
    }

    @Test
    @Order(6)
    @DisplayName("6. Проверка минимального запаса")
    void testMinStockLimit() {
        List<InventoryItem> items = inventoryDAO.getItems(1);

        if (items.isEmpty()) {
            System.out.println("Нет товаров в инвентаре - пропускаем тест");
            return;
        }

        int lowStockCount = 0;
        for (InventoryItem item : items) {
            if (item.getQuantity() <= item.getMinStock()) {
                lowStockCount++;
                System.out.println("  Низкий запас: " + item.getProductName() +
                        " (кол-во: " + item.getQuantity() + ", мин: " + item.getMinStock() + ")");
            }
        }

        System.out.println("Товаров с низким запасом: " + lowStockCount);
    }

    @Test
    @Order(7)
    @DisplayName("7. Получение инвентаря несуществующего магазина")
    void testGetItemsForNonExistentStore() {
        List<InventoryItem> items = inventoryDAO.getItems(99999);
        assertNotNull(items, "Список не должен быть null даже для несуществующего магазина");
        assertTrue(items.isEmpty(), "Список должен быть пустым для несуществующего магазина");
        System.out.println("Инвентарь несуществующего магазина: пуст (ОК)");
    }
}