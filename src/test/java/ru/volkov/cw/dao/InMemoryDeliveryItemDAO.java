package ru.volkov.cw.dao;

import ru.volkov.cw.model.DeliveryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory реализация DeliveryItemDAO для тестирования.
 */
public class InMemoryDeliveryItemDAO extends DeliveryItemDAO {

    private final List<DeliveryItem> items = new ArrayList<>();

    @Override
    public List<DeliveryItem> getItemsByDelivery(int deliveryId) {
        List<DeliveryItem> result = new ArrayList<>();
        for (DeliveryItem item : items) {
            if (item.getDeliveryId() == deliveryId) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public boolean addItem(DeliveryItem item) {
        items.add(item);
        return true;
    }

    @Override
    public boolean updateItem(DeliveryItem item) {
        for (int i = 0; i < items.size(); i++) {
            DeliveryItem existing = items.get(i);
            if (existing.getDeliveryId() == item.getDeliveryId()
                    && existing.getProductId() == item.getProductId()) {
                items.set(i, item);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteItem(int deliveryId, int productId) {
        return items.removeIf(item ->
                item.getDeliveryId() == deliveryId && item.getProductId() == productId);
    }

    public void clear() {
        items.clear();
    }
}