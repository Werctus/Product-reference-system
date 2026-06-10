package ru.volkov.cw.model;

/**
 * Модель для отображения остатков товаров в магазине.
 */
public class InventoryItem {
    private int productId;
    private String productName;
    private int quantity;
    private int minStock;
    private String characteristic;

    public InventoryItem() {}

    public InventoryItem(int productId, String productName, int quantity, int minStock, String characteristic) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.minStock = minStock;
        this.characteristic = characteristic;
    }

    // Геттеры и сеттеры
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }

    public String getCharacteristic() { return characteristic; }
    public void setCharacteristic(String characteristic) { this.characteristic = characteristic; }
}