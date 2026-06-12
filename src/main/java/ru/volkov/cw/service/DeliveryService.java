package ru.volkov.cw.service;

import ru.volkov.cw.dao.DeliveryDAO;
import ru.volkov.cw.dao.DeliveryItemDAO;
import ru.volkov.cw.model.Delivery;

public class DeliveryService {

    private final DeliveryDAO deliveryDAO;
    private final DeliveryItemDAO deliveryItemDAO;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";

    public DeliveryService(DeliveryDAO deliveryDAO, DeliveryItemDAO deliveryItemDAO) {
        this.deliveryDAO = deliveryDAO;
        this.deliveryItemDAO = deliveryItemDAO;
    }

    /**
     * Удаляет поставку, если она не проведена.
     * @throws IllegalStateException если поставка уже проведена
     */
    public boolean deleteDelivery(int deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        if (delivery == null) {
            throw new IllegalArgumentException("Поставка не найдена");
        }

        // ПРОВЕРКА СТАТУСА - нельзя удалять проведенные поставки
        if (STATUS_COMPLETED.equals(delivery.getStatus())) {
            throw new IllegalStateException("Нельзя удалить проведенную поставку");
        }

        return deliveryDAO.deleteDelivery(deliveryId);
    }

    private Delivery getDeliveryById(int id) {
        return deliveryDAO.getAllDeliveries().stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void completeDelivery(int deliveryId) {
        System.out.println("Проведение поставки ID: " + deliveryId);
    }
}