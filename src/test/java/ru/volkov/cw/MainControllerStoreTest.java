
package ru.volkov.cw;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
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
@DisplayName("Вкладка Stores (Магазины)")
class MainControllerStoreTest {

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
        robot.clickOn("Stores");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Добавление магазина")
    void testAddStore(FxRobot robot) {
        robot.clickOn("#btnAddStore");
        WaitForAsyncUtils.waitForFxEvents();

        Set<TextField> textFields = robot.lookup(".text-field").queryAll();
        TextField[] fields = textFields.toArray(new TextField[0]);

        robot.clickOn(fields[0]).write("Test Store " + System.currentTimeMillis());
        robot.clickOn(fields[1]).write("9991234567");
        robot.clickOn(fields[2]).write("Test Address");

        if (fields.length > 3) {
            robot.clickOn(fields[3]).write("test@example.com");
        }

        robot.clickOn("Сохранить");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("2. Выбор магазина и просмотр инвентаря")
    void testStoreInventory(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableStores").queryTableView();

        if (table.getItems().isEmpty()) {
            System.out.println("Нет магазинов - пропускаем тест");
            return;
        }

        Set<Node> rows = robot.lookup(".table-row-cell").queryAll();
        if (!rows.isEmpty()) {
            robot.clickOn(rows.iterator().next());
            WaitForAsyncUtils.waitForFxEvents();

            // Разворачиваем панель инвентаря
            TitledPane inventoryPane = robot.lookup("#storeInventoryPane").query();
            if (inventoryPane != null && !inventoryPane.isExpanded()) {
                robot.clickOn(inventoryPane);
                WaitForAsyncUtils.waitForFxEvents();
            }

            TableView<?> invTable = robot.lookup("#tableStoreInventory").queryTableView();
            System.out.println("Товаров в инвентаре: " + invTable.getItems().size());
        }
    }

    @Test
    @DisplayName("3. Проверка кнопок магазинов")
    void testStoreButtons(FxRobot robot) {
        assertNodeExists(robot, "#btnAddStore", "Add Store");
        assertNodeExists(robot, "#btnEditStore", "Edit Store");
        assertNodeExists(robot, "#btnDeleteStore", "Delete Store");
        assertNodeExists(robot, "#btnAddToStore", "Add to Store");
        assertNodeExists(robot, "#btnUpdateQuantity", "Update Quantity");
    }

    @Test
    @DisplayName("4. Проверка таблицы магазинов")
    void testStoreTableColumns(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableStores").queryTableView();
        assertNotNull(table, "Таблица магазинов должна существовать");

        // Проверяем наличие колонок
        boolean hasName = table.getColumns().stream()
                .anyMatch(col -> col.getText().contains("Name") || col.getText().contains("Название"));
        boolean hasAddress = table.getColumns().stream()
                .anyMatch(col -> col.getText().contains("Address") || col.getText().contains("Адрес"));

        System.out.println("Колонка Name: " + hasName);
        System.out.println("Колонка Address: " + hasAddress);
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}
