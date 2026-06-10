package ru.volkov.cw.service;

public interface InventoryService {
    int getTotalProductsCount();
    int getLowStockProductsCount();
    int getTotalStoresCount();
}