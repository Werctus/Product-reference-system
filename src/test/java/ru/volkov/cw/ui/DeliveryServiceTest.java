package ru.volkov.cw.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.volkov.cw.dao.InMemoryDeliveryDAO;
import ru.volkov.cw.dao.InMemoryDeliveryItemDAO;
import ru.volkov.cw.model.Delivery;
import ru.volkov.cw.service.DeliveryService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты сервисного слоя для работы с поставками.
 * Тест 4: rejectsCompletedDeliveryDeletion (отрицательный)
 */
public class DeliveryServiceTest {

    private DeliveryService deliveryService;
    private InMemoryDeliveryDAO deliveryDAO;
    private InMemoryDeliveryItemDAO deliveryItemDAO;

    @BeforeEach
    public void setUp() {
        deliveryDAO = new InMemoryDeliveryDAO();
        deliveryItemDAO = new InMemoryDeliveryItemDAO();
        deliveryService = new DeliveryService(deliveryDAO, deliveryItemDAO);
    }

    /**
     * Тест 4: Попытка удалить поставку со статусом "Проведен".
     * Ожидаемый результат: сервис отклоняет операцию.
     */
    @Test
    public void rejectsCompletedDeliveryDeletion() {
        // Arrange: создаем поставку
        Delivery delivery = new Delivery("DOC-001", 1, 1);
        int deliveryId = deliveryDAO.addDelivery(delivery);

        // Устанавливаем статус "Проведен" (completed)
        deliveryDAO.setDeliveryStatus(deliveryId, DeliveryService.STATUS_COMPLETED);

        // Act & Assert: пытаемся удалить проведенную поставку
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deliveryService.deleteDelivery(deliveryId),
                "Должно выброситься исключение при попытке удалить проведенную поставку"
        );

        // Проверяем сообщение об ошибке
        assertTrue(exception.getMessage().contains("Нельзя удалить проведенную поставку"),
                "Сообщение об ошибке должно указывать на запрет удаления");

        // Проверяем, что поставка НЕ была удалена
        assertEquals(1, deliveryDAO.getAllDeliveries().size(),
                "Проведенная поставка не должна быть удалена");

        System.out.println("✅ Тест 4 пройден: отклонение удаления проведенной поставки");
    }

    /**
     * Дополнительный тест: удаление черновика поставки должно работать.
     */
    @Test
    public void allowsDraftDeliveryDeletion() {
        // Arrange: создаем поставку со статусом "Черновик" (pending)
        Delivery delivery = new Delivery("DOC-002", 1, 1);
        int deliveryId = deliveryDAO.addDelivery(delivery);

        // Act: удаляем черновик
        boolean result = deliveryService.deleteDelivery(deliveryId);

        // Assert: поставка должна быть удалена
        assertTrue(result, "Черновик поставки должен быть успешно удален");
        assertEquals(0, deliveryDAO.getAllDeliveries().size(),
                "Черновик поставки должен быть удален из хранилища");

        System.out.println("✅ Дополнительный тест пройден: удаление черновика поставки");
    }
}