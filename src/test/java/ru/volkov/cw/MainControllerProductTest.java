package ru.volkov.cw;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@DisplayName("Вкладка Products (Товары)")
class MainControllerProductTest {

    @Start
    public void start(Stage stage) {
        try {
            new MainApplication().start(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Products");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Добавление нового продукта")
    void testAddProduct(FxRobot robot) {
        robot.clickOn("#btnAddProduct");
        WaitForAsyncUtils.waitForFxEvents();

        // Проверяем, что форма открылась
        TitledPane form = robot.lookup("#productFormPane").query();
        assertNotNull(form, "Форма продукта должна быть открыта");

        // Заполняем поля
        robot.clickOn("#txtProductName").write("Test Product " + System.currentTimeMillis());

        TextField priceField = robot.lookup("#txtProductPrice").query();
        robot.doubleClickOn(priceField);
        robot.write("99.99");

        TextField discountField = robot.lookup("#txtProductDiscount").query();
        robot.doubleClickOn(discountField);
        robot.write("10.00");

        robot.clickOn("#txtProductCharacteristic").write("Test characteristic");

        // Сохраняем
        robot.clickOn("#btnSaveProduct");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("2. Поиск продуктов")
    void testSearchProduct(FxRobot robot) {
        TextField searchField = robot.lookup("#txtSearchProduct").query();
        assertNotNull(searchField, "Поле поиска должно существовать");

        robot.clickOn(searchField);
        robot.write("test");
        WaitForAsyncUtils.waitForFxEvents();

        TableView<?> table = robot.lookup("#tableProducts").queryTableView();
        System.out.println("Продуктов после фильтрации: " + table.getItems().size());
    }

    @Test
    @DisplayName("3. Обновление списка продуктов")
    void testRefreshProducts(FxRobot robot) {
        robot.clickOn("#btnRefreshProduct");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("4. Отмена редактирования продукта")
    void testCancelEdit(FxRobot robot) {
        robot.clickOn("#btnAddProduct");
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#txtProductName").write("Cancel Product");
        robot.clickOn("#btnCancelProduct");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("5. Выбор продукта в таблице")
    void testSelectProduct(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableProducts").queryTableView();

        if (table.getItems().isEmpty()) {
            System.out.println("Нет продуктов - пропускаем тест");
            return;
        }

        Set<Node> rows = robot.lookup(".table-row-cell").queryAll();
        if (!rows.isEmpty()) {
            Node firstRow = rows.iterator().next();
            robot.clickOn(firstRow);
            WaitForAsyncUtils.waitForFxEvents();

            TextField nameField = robot.lookup("#txtProductName").query();
            System.out.println("Выбран продукт: " + nameField.getText());
        }
    }

    @Test
    @DisplayName("6. Проверка кнопок на вкладке Products")
    void testProductButtons(FxRobot robot) {
        assertNodeExists(robot, "#btnAddProduct", "Add");
        assertNodeExists(robot, "#btnEditProduct", "Edit");
        assertNodeExists(robot, "#btnDeleteProduct", "Delete");
        assertNodeExists(robot, "#btnRefreshProduct", "Refresh");
        assertNodeExists(robot, "#btnSearchProduct", "Search");
        assertNodeExists(robot, "#txtSearchProduct", "Search field");
        assertNodeExists(robot, "#cbFilterCategory", "Filter category");
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}
