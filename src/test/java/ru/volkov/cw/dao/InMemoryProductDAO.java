package ru.volkov.cw.dao;

import ru.volkov.cw.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory реализация ProductDAO для тестирования.
 */
public class InMemoryProductDAO extends ProductDAO {

    private final List<Product> products = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public boolean addProduct(Product product) {
        int newId = idCounter.getAndIncrement();
        Product newProduct = new Product(
                newId,
                product.getName(),
                product.getArticleNumber(),
                product.getPrice(),
                product.getCharacteristic(),
                product.getDiscount(),
                product.getVat(),
                product.getBrandId(),
                product.getUnitId(),
                product.getCategoryId(),
                product.getSubcategoryId(),
                product.getImage()
        );
        products.add(newProduct);
        return true;
    }

    @Override
    public boolean updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }

    @Override
    public String generateNewArticle(int categoryId) {
        return "ART-" + idCounter.get();
    }

    public void clear() {
        products.clear();
        idCounter.set(1);
    }
}