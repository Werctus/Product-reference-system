package ru.volkov.cw.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.volkov.cw.dao.InMemoryProductDAO;
import ru.volkov.cw.model.Product;
import ru.volkov.cw.service.ProductService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты сервисного слоя для работы с товарами.
 * Тест 2: createsNewProductSuccessfully (положительный)
 * Тест 3: rejectsProductCreationWithInvalidPrice (отрицательный)
 */
public class ProductServiceTest {

    private ProductService productService;
    private InMemoryProductDAO productDAO;

    @BeforeEach
    public void setUp() {
        productDAO = new InMemoryProductDAO();
        productService = new ProductService(productDAO);
    }

    /**
     * Тест 2: Успешное создание товара с заполнением всех обязательных полей.
     * Ожидаемый результат: товар сохраняется, генерируется артикул.
     */
    @Test
    public void createsNewProductSuccessfully() {
        // Arrange: создаем товар со всеми обязательными полями
        Product product = new Product();
        product.setName("Тестовый товар");
        product.setPrice(new BigDecimal("100.50"));
        product.setBrandId(1);
        product.setUnitId(1);
        product.setCategoryId(1);

        // Act: сохраняем товар через сервис
        boolean result = productService.saveProduct(product);

        // Assert: проверяем, что товар сохранен
        assertTrue(result, "Товар должен быть успешно сохранен");

        // Проверяем, что товар появился в хранилище
        assertEquals(1, productDAO.getAllProducts().size(),
                "В хранилище должен быть один товар");

        Product savedProduct = productDAO.getAllProducts().get(0);
        assertEquals("Тестовый товар", savedProduct.getName());
        assertEquals(new BigDecimal("100.50"), savedProduct.getPrice());

        // Проверяем генерацию артикула
        assertNotNull(savedProduct.getArticleNumber(),
                "Артикул должен быть сгенерирован");
        assertTrue(savedProduct.getArticleNumber().startsWith("ART-"),
                "Артикул должен начинаться с ART-");

        System.out.println("✅ Тест 2 пройден: успешное создание товара");
    }

    /**
     * Тест 3: Попытка сохранить товар с некорректным форматом цены.
     * Ожидаемый результат: валидация отклоняет операцию.
     */
    @Test
    public void rejectsProductCreationWithInvalidPrice() {
        // Arrange: пытаемся валидировать некорректную цену (текст вместо числа)
        String invalidPrice = "abc123";

        // Act & Assert: проверяем, что валидация выбрасывает исключение
        NumberFormatException exception = assertThrows(
                NumberFormatException.class,
                () -> productService.validatePriceString(invalidPrice),
                "Должно выброситься исключение при некорректном формате цены"
        );

        // Проверяем сообщение об ошибке
        assertTrue(exception.getMessage().contains("Некорректный формат цены"),
                "Сообщение об ошибке должно указывать на некорректный формат");

        // Проверяем, что товар НЕ был сохранен
        assertEquals(0, productDAO.getAllProducts().size(),
                "Товар не должен быть сохранен при некорректной цене");

        System.out.println("✅ Тест 3 пройден: отклонение товара с некорректной ценой");
    }
}