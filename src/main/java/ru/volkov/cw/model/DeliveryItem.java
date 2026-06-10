package ru.volkov.cw.model;

import java.math.BigDecimal;

public class DeliveryItem {
    private int id;
    private int deliveryId;
    private int productId;
    private String productName; // Название берем из таблицы product
    private int quantity;
    private BigDecimal total;

    public DeliveryItem() {}

    public DeliveryItem(int id, int deliveryId, int productId, String productName, int quantity, BigDecimal total) {
        this.id = id;
        this.deliveryId = deliveryId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.total = total;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDeliveryId() { return deliveryId; }
    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return total; }
    public void setPrice(BigDecimal price) { this.total = total; }

    // Автоматический расчет суммы (цена * количество)
    public BigDecimal getTotal() {
        if (total != null) {
            return total.multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
}