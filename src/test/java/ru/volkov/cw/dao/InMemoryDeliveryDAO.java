package ru.volkov.cw.dao;

import ru.volkov.cw.model.Delivery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory реализация DeliveryDAO для тестирования.
 */
public class InMemoryDeliveryDAO extends DeliveryDAO {

    private final List<Delivery> deliveries = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public List<Delivery> getAllDeliveries() {
        return new ArrayList<>(deliveries);
    }

    @Override
    public int addDelivery(Delivery del) {
        int newId = idCounter.getAndIncrement();
        Delivery newDel = new Delivery(
                newId,
                "DOC-" + newId,
                "pending",
                del.getCompanyId(),
                del.getStoreId(),
                LocalDateTime.now()
        );
        deliveries.add(newDel);
        return newId;
    }

    @Override
    public boolean deleteDelivery(int id) {
        return deliveries.removeIf(d -> d.getId() == id);
    }

    /**
     * Метод для тестов: установить статус поставки напрямую.
     */
    public void setDeliveryStatus(int id, String status) {
        deliveries.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .ifPresent(d -> d.setStatus(status));
    }

    /**
     * Метод для тестов: очистить все данные.
     */
    public void clear() {
        deliveries.clear();
        idCounter.set(1);
    }
}