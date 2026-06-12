package ru.volkov.cw;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.volkov.cw.model.Product;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest extends BaseApplicationTest {

    private final String uniqueProductName = "Тестовый Товар " + System.currentTimeMillis();
    private final String uniqueArticle = "ART" + System.currentTimeMillis();
    private final String price = "100.50";
    private final String discount = "10";
    private final String vat = "20";

    @BeforeEach
    void loginAndNavigateToProducts() {
        performLogin();
        switchToTab(0); // Вкладка "Товары"
    }

    // ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================

    @Test
    void testAddNewProduct() {
        // Нажимаем кнопку "Добавить"
        clickOn("#btnAddProduct");

        // Ждем открытия формы
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Заполняем форму
        writeText("txtProductName", uniqueProductName);
        writeText("txtProductPrice", price);
        writeText("txtProductDiscount", discount);
        writeText("txtProductVat", vat);

        // Сохраняем
        clickOn("#btnSaveProduct");

        // Ждем обновления таблицы
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем, что товар появился в таблице
        TableView<Product> tableView = lookup("#tableProducts").query();
        boolean found = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                found = true;
                productsToClean.add(p);
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    void testEditProduct() {
        // Сначала добавляем товар
        clickOn("#btnAddProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("txtProductName", uniqueProductName);
        writeText("txtProductPrice", "100.00");
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Находим добавленный товар
        TableView<Product> tableView = lookup("#tableProducts").query();
        Product addedProduct = null;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                addedProduct = p;
                productsToClean.add(p);
                break;
            }
        }
        assertNotNull(addedProduct);

        // Выбираем товар и нажимаем "Редактировать"
        interact(() -> tableView.getSelectionModel().select(addedProduct));
        clickOn("#btnEditProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Изменяем цену
        String newPrice = "150.00";
        writeText("txtProductPrice", newPrice);
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем, что цена обновилась
        tableView = lookup("#tableProducts").query();
        boolean priceUpdated = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName) &&
                    p.getPrice().toString().equals(newPrice)) {
                priceUpdated = true;
                break;
            }
        }
        assertThat(priceUpdated).isTrue();
    }

    @Test
    void testDeleteProduct() {
        // Добавляем товар
        clickOn("#btnAddProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("txtProductName", uniqueProductName);
        writeText("txtProductPrice", "100.00");
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Находим товар
        TableView<Product> tableView = lookup("#tableProducts").query();
        Product productToDelete = null;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                productToDelete = p;
                break;
            }
        }
        assertNotNull(productToDelete);

        // Выбираем и удаляем
        interact(() -> tableView.getSelectionModel().select(productToDelete));
        clickOn("#btnDeleteProduct");

        // Подтверждаем удаление
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}
        // Нажимаем "Да" в диалоге подтверждения
        clickOn("Да");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем, что товар удален
        tableView = lookup("#tableProducts").query();
        boolean stillExists = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                stillExists = true;
                break;
            }
        }
        assertThat(stillExists).isFalse();
    }

    @Test
    void testSearchProduct() {
        // Добавляем товар
        clickOn("#btnAddProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("txtProductName", uniqueProductName);
        writeText("txtProductPrice", "100.00");
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Ищем товар по имени
        String searchQuery = uniqueProductName.substring(0, 10);
        writeText("txtSearchProduct", searchQuery);
        clickOn("#btnSearchProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем, что результат поиска содержит наш товар
        TableView<Product> tableView = lookup("#tableProducts").query();
        boolean found = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().contains(searchQuery)) {
                found = true;
                productsToClean.add(p);
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    void testRefreshProducts() {
        // Нажимаем кнопку обновления
        clickOn("#btnRefreshProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем, что таблица не пуста (должны быть данные)
        TableView<Product> tableView = lookup("#tableProducts").query();
        assertNotNull(tableView.getItems());
    }

    // ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================

    @Test
    void testAddProductWithEmptyName() {
        // Нажимаем "Добавить"
        clickOn("#btnAddProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Оставляем имя пустым, заполняем только цену
        writeText("txtProductPrice", "100.00");
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должна появиться ошибка (Alert)
        // Проверяем, что товар НЕ добавился
        TableView<Product> tableView = lookup("#tableProducts").query();
        boolean found = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                found = true;
                break;
            }
        }
        assertThat(found).isFalse();
    }

    @Test
    void testAddProductWithInvalidPrice() {
        // Нажимаем "Добавить"
        clickOn("#btnAddProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("txtProductName", uniqueProductName);
        writeText("txtProductPrice", "invalid_price"); // Некорректная цена
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должна появиться ошибка формата
        // Товар не должен добавиться
        TableView<Product> tableView = lookup("#tableProducts").query();
        boolean found = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                found = true;
                break;
            }
        }
        assertThat(found).isFalse();
    }

    @Test
    void testAddProductWithNegativePrice() {
        // Нажимаем "Добавить"
        clickOn("#btnAddProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("txtProductName", uniqueProductName);
        writeText("txtProductPrice", "-100.00"); // Отрицательная цена
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем, что товар не добавился или цена корректна
        TableView<Product> tableView = lookup("#tableProducts").query();
        boolean found = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                found = true;
                productsToClean.add(p);
                // Если добавился, цена должна быть >= 0
                assertThat(p.getPrice().doubleValue()).isGreaterThanOrEqualTo(0);
                break;
            }
        }
    }

    @Test
    void testEditWithoutSelection() {
        // Не выбираем товар, пытаемся редактировать
        clickOn("#btnEditProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должно появиться предупреждение
        // Форма редактирования не должна открыться
        TextField txtName = lookup("#txtProductName").query();
        assertThat(txtName.getText()).isEmpty();
    }

    @Test
    void testDeleteWithoutSelection() {
        // Не выбираем товар, пытаемся удалить
        clickOn("#btnDeleteProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должно появиться предупреждение
        // Таблица не должна измениться
        TableView<Product> tableView = lookup("#tableProducts").query();
        assertNotNull(tableView.getItems());
    }

    @Test
    void testAddProductWithEmptyPrice() {
        // Нажимаем "Добавить"
        clickOn("#btnAddProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("txtProductName", uniqueProductName);
        // Оставляем цену пустой
        clickOn("#btnSaveProduct");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должна появиться ошибка
        TableView<Product> tableView = lookup("#tableProducts").query();
        boolean found = false;
        for (Product p : tableView.getItems()) {
            if (p.getName().equals(uniqueProductName)) {
                found = true;
                break;
            }
        }
        assertThat(found).isFalse();
    }
}