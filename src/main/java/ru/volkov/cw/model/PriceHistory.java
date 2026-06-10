package ru.volkov.cw.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceHistory {
    private int id;
    private int productId;
    private String productName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private LocalDateTime changedAt;
    private int changedBy; // userId или null
    private String changedByUser;

    // Конструкторы
    public PriceHistory() {}

    public PriceHistory(int productId, BigDecimal oldPrice, BigDecimal newPrice) {
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }

    public BigDecimal getNewPrice() { return newPrice; }
    public void setNewPrice(BigDecimal newPrice) { this.newPrice = newPrice; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public int getChangedBy() { return changedBy; }
    public void setChangedBy(int changedBy) { this.changedBy = changedBy; }

    public String getChangedByUser() { return changedByUser; }
    public void setChangedByUser(String changedByUser) { this.changedByUser = changedByUser; }
}