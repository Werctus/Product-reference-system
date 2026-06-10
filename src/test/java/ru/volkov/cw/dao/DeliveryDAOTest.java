// =====================================================
// Файл: DeliveryDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.Delivery;
import ru.volkov.cw.model.DeliveryItem;

import java.math.BigDecimal;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("DeliveryDAO - Работа с поставками")
class DeliveryDAOTest {

    private static DeliveryDAO deliveryDAO;
    private static DeliveryItemDAO deliveryItemDAO;
    private static int testDeliveryId;

    @BeforeAll
    static void setUp() {
        deliveryDAO = new DeliveryDAO();
        deliveryItemDAO = new DeliveryItemDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех поставок")
    void testGetAllDeliveries() {
        List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
        assertNotNull(deliveries, "Список поставок не должен быть null");
        System.out.println("Количество поставок в БД: " + deliveries.size());

        for (Delivery d : deliveries) {
            System.out.println("  - Поставка #" + d.getDocumentNumber() + " (Статус: " + d.getStatus() + ")");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Добавление новой поставки")
    void testAddDelivery() {
        Delivery delivery = new Delivery();
        delivery.setDocumentNumber("TEST-" + System.currentTimeMillis());
        delivery.setCompanyId(1);
        delivery.setStoreId(1);
        delivery.setStatus("draft");

        int id = deliveryDAO.addDelivery(delivery);
        assertTrue(id > 0, "ID поставки должен быть положительным числом");
        testDeliveryId = id;
        System.out.println("Добавлена поставка с ID: " + id + ", номер: " + delivery.getDocumentNumber());
    }

    @Test
    @Order(3)
    @DisplayName("3. Добавление товара в поставку")
    void testAddDeliveryItem() {
        if (testDeliveryId == 0) {
            System.out.println("Нет тестовой поставки - пропускаем тест");
            return;
        }

        DeliveryItem item = new DeliveryItem();
        item.setDeliveryId(testDeliveryId);
        item.setProductId(1);
        item.setQuantity(10);
        item.setPrice(new BigDecimal("150.00"));

        boolean result = deliveryItemDAO.addItem(item);
        assertTrue(result, "Товар должен быть добавлен в поставку");
        System.out.println("Добавлен товар в поставку " + testDeliveryId + ": кол-во=" + item.getQuantity() + ", цена=" + item.getPrice());
    }

    @Test
    @Order(4)
    @DisplayName("4. Получение товаров поставки")
    void testGetDeliveryItems() {
        if (testDeliveryId == 0) {
            System.out.println("Нет тестовой поставки - пропускаем тест");
            return;
        }

        List<DeliveryItem> items = deliveryItemDAO.getItemsByDelivery(testDeliveryId);
        assertNotNull(items, "Список товаров поставки не должен быть null");
        System.out.println("Товаров в поставке " + testDeliveryId + ": " + items.size());

        for (DeliveryItem item : items) {
            System.out.println("  - Товар ID: " + item.getProductId() + ", кол-во: " + item.getQuantity() + ", цена: " + item.getPrice() + ", сумма: " + item.getTotal());
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. Обновление товара в поставке")
    void testUpdateDeliveryItem() {
        List<DeliveryItem> items = deliveryItemDAO.getItemsByDelivery(testDeliveryId > 0 ? testDeliveryId : 1);

        if (items.isEmpty()) {
            System.out.println("Нет товаров для обновления - пропускаем тест");
            return;
        }

        DeliveryItem item = items.get(0);
        int originalQty = item.getQuantity();
        BigDecimal originalPrice = item.getPrice();

        // Изменяем количество и цену
        item.setQuantity(originalQty + 5);
        item.setPrice(originalPrice.add(new BigDecimal("25.00")));

        boolean result = deliveryItemDAO.updateItem(item);
        assertTrue(result, "Товар в поставке должен быть обновлен");
        System.out.println("Товар обновлен: новое кол-во=" + item.getQuantity() + ", новая цена=" + item.getPrice());

        // Возвращаем исходные данные
        item.setQuantity(originalQty);
        item.setPrice(originalPrice);
        deliveryItemDAO.updateItem(item);
        System.out.println("Данные товара возвращены к исходным");
    }

    @Test
    @Order(6)
    @DisplayName("6. Удаление товара из поставки")
    void testDeleteDeliveryItem() {
        // Добавляем временный товар для удаления
        int deliveryId = testDeliveryId > 0 ? testDeliveryId : 1;

        DeliveryItem tempItem = new DeliveryItem();
        tempItem.setDeliveryId(deliveryId);
        tempItem.setProductId(1);
        tempItem.setQuantity(1);
        tempItem.setPrice(new BigDecimal("1.00"));
        deliveryItemDAO.addItem(tempItem);

        List<DeliveryItem> items = deliveryItemDAO.getItemsByDelivery(deliveryId);
        if (items.isEmpty()) {
            System.out.println("Нет товаров для удаления - пропускаем тест");
            return;
        }

        DeliveryItem item = items.get(items.size() - 1);
        boolean result = deliveryItemDAO.deleteItem(item.getDeliveryId(), item.getProductId());
        assertTrue(result, "Товар должен быть удален из поставки");
        System.out.println("Товар удален из поставки " + item.getDeliveryId());
    }

    @Test
    @Order(7)
    @DisplayName("7. Расчет общей суммы поставки")
    void testDeliveryTotalCalculation() {
        List<DeliveryItem> items = deliveryItemDAO.getItemsByDelivery(testDeliveryId > 0 ? testDeliveryId : 1);

        BigDecimal total = BigDecimal.ZERO;
        for (DeliveryItem item : items) {
            if (item.getTotal() != null) {
                total = total.add(item.getTotal());
            }
        }

        System.out.println("Общая сумма поставки: " + total);
        System.out.println("Количество позиций: " + items.size());
    }
}