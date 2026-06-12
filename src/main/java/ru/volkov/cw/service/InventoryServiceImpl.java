package ru.volkov.cw.service;

import ru.volkov.cw.dao.InventoryDAO;
import ru.volkov.cw.dao.ProductDAO;
import ru.volkov.cw.dao.StoreDAO;

public class InventoryServiceImpl implements InventoryService {

    private final ProductDAO productDAO;
    private final StoreDAO storeDAO;
    private final InventoryDAO inventoryDAO;

    public InventoryServiceImpl(ProductDAO productDAO, StoreDAO storeDAO, InventoryDAO inventoryDAO) {
        this.productDAO = productDAO;
        this.storeDAO = storeDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public int getTotalProductsCount() {
        return productDAO.getAllProducts().size();
    }

    @Override
    public int getLowStockProductsCount() {
        return 0; // Упрощенная версия
    }

    @Override
    public int getTotalStoresCount() {
        return storeDAO.getAllStores().size();
    }
}