package ru.volkov.cw.service;

import ru.volkov.cw.dao.ProductDAO;
import ru.volkov.cw.model.Product;
import java.math.BigDecimal;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название товара не может быть пустым");
        }

        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Цена не может быть пустой");
        }

        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
    }

    public BigDecimal validatePriceString(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Цена не может быть пустой");
        }

        try {
            BigDecimal price = new BigDecimal(priceStr.replace(",", ".").trim());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Цена не может быть отрицательной");
            }
            return price;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Некорректный формат цены: " + priceStr);
        }
    }

    public boolean saveProduct(Product product) {
        validateProduct(product);

        if (product.getId() == 0) {
            if (product.getArticleNumber() == null || product.getArticleNumber().isEmpty()) {
                String article = productDAO.generateNewArticle(
                        product.getCategoryId() != null ? product.getCategoryId() : 0
                );
                product.setArticleNumber(article);
            }
            return productDAO.addProduct(product);
        } else {
            return productDAO.updateProduct(product);
        }
    }
}