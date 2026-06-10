package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.Product;
import ru.volkov.cw.model.Brand;

import java.math.BigDecimal;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ProductDAO - Работа с товарами")
class ProductDAOTest {

    private static ProductDAO productDAO;
    private static BrandDAO brandDAO;
    private static int testProductId;

    @BeforeAll
    static void setUp() {
        productDAO = new ProductDAO();
        brandDAO = new BrandDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех продуктов")
    void testGetAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        assertNotNull(products, "Список продуктов не должен быть null");
        System.out.println("Количество продуктов в БД: " + products.size());

        if (!products.isEmpty()) {
            Product first = products.get(0);
            System.out.println("Первый продукт: " + first.getName() + " (ID: " + first.getId() + ")");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Добавление нового продукта")
    void testAddProduct() {
        // Создаем тестовый бренд
        Brand brand = new Brand("TestBrand_" + System.currentTimeMillis());
        brandDAO.addBrand(brand);

        // Получаем ID добавленного бренда
        List<Brand> brands = brandDAO.getAllBrands();
        Brand addedBrand = brands.stream()
                .filter(b -> b.getName().equals(brand.getName()))
                .findFirst()
                .orElse(null);

        int brandId = addedBrand != null ? addedBrand.getId() : 1;

        Product product = new Product();
        product.setName("Test Product " + System.currentTimeMillis());
        product.setArticleNumber("ART" + System.currentTimeMillis());
        product.setPrice(new BigDecimal("99.99"));
        product.setBrandId(brandId);
        product.setUnitId(1);
        product.setVat(new BigDecimal("20.00"));
        product.setDiscount(BigDecimal.ZERO);
        product.setCharacteristic("Test characteristic");

        boolean result = productDAO.addProduct(product);
        assertTrue(result, "Продукт должен быть успешно добавлен");

        // Проверяем, что продукт появился в базе
        List<Product> products = productDAO.getAllProducts();
        Product added = products.stream()
                .filter(p -> product.getName().equals(p.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(added, "Добавленный продукт должен быть найден в БД");
        testProductId = added.getId();
        System.out.println("Добавлен продукт с ID: " + testProductId + ", название: " + added.getName());
    }

    @Test
    @Order(3)
    @DisplayName("3. Обновление продукта")
    void testUpdateProduct() {
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Нет продуктов для обновления - пропускаем тест");
            return;
        }

        Product product = products.get(0);
        String originalName = product.getName();
        BigDecimal originalPrice = product.getPrice();

        // Изменяем данные
        product.setName(originalName + " (Updated)");
        product.setPrice(originalPrice.add(new BigDecimal("50.00")));
        product.setDiscount(new BigDecimal("15.00"));

        boolean result = productDAO.updateProduct(product);
        assertTrue(result, "Продукт должен быть успешно обновлен");
        System.out.println("Продукт обновлен: " + product.getName() + ", новая цена: " + product.getPrice());

        // Возвращаем исходные данные
        product.setName(originalName);
        product.setPrice(originalPrice);
        product.setDiscount(BigDecimal.ZERO);
        productDAO.updateProduct(product);
        System.out.println("Данные продукта возвращены к исходным");
    }

    @Test
    @Order(4)
    @DisplayName("4. Генерация артикула")
    void testGenerateArticle() {
        String article = productDAO.generateNewArticle(1);
        assertNotNull(article, "Артикул не должен быть null");
        assertFalse(article.isEmpty(), "Артикул не должен быть пустым");
        System.out.println("Сгенерирован артикул: " + article);

        // Проверяем, что артикул состоит из цифр
        assertTrue(article.matches("\\d+"), "Артикул должен состоять из цифр");
    }

    @Test
    @Order(5)
    @DisplayName("5. Удаление продукта")
    void testDeleteProduct() {
        if (testProductId == 0) {
            // Создаем продукт для удаления
            Product product = new Product();
            product.setName("DeleteTest_" + System.currentTimeMillis());
            product.setArticleNumber("DEL" + System.currentTimeMillis());
            product.setPrice(new BigDecimal("1.00"));
            product.setBrandId(1);
            product.setUnitId(1);
            productDAO.addProduct(product);

            List<Product> products = productDAO.getAllProducts();
            Product added = products.stream()
                    .filter(p -> product.getName().equals(p.getName()))
                    .findFirst()
                    .orElse(null);

            if (added != null) {
                testProductId = added.getId();
            } else {
                System.out.println("Не удалось создать продукт для удаления");
                return;
            }
        }

        boolean result = productDAO.deleteProduct(testProductId);
        assertTrue(result, "Продукт должен быть успешно удален");
        System.out.println("Продукт с ID " + testProductId + " удален");

        // Проверяем, что продукт действительно удален
        List<Product> products = productDAO.getAllProducts();
        boolean exists = products.stream().anyMatch(p -> p.getId() == testProductId);
        assertFalse(exists, "Продукт не должен существовать после удаления");
    }

    @Test
    @Order(6)
    @DisplayName("6. Проверка обязательных полей продукта")
    void testProductRequiredFields() {
        Product product = new Product();
        product.setName("FieldTest_" + System.currentTimeMillis());
        product.setArticleNumber("FIELDTEST");
        product.setPrice(new BigDecimal("50.00"));
        product.setBrandId(1);
        product.setUnitId(1);

        boolean result = productDAO.addProduct(product);
        assertTrue(result, "Продукт с обязательными полями должен быть добавлен");

        // Проверяем значения по умолчанию
        List<Product> products = productDAO.getAllProducts();
        Product added = products.stream()
                .filter(p -> "FIELDTEST".equals(p.getArticleNumber()))
                .findFirst()
                .orElse(null);

        if (added != null) {
            System.out.println("VAT: " + added.getVat());
            System.out.println("Discount: " + added.getDiscount());

            // Удаляем тестовый продукт
            productDAO.deleteProduct(added.getId());
        }
    }
}