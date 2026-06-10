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
@DisplayName("Вкладка Deliveries (Поставки)")
class MainControllerDeliveryTest {

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
        robot.clickOn("Deliveries");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Проверка таблицы поставок")
    void testDeliveryTable(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableDeliveries").queryTableView();
        assertNotNull(table, "Таблица поставок должна существовать");
        System.out.println("Количество поставок: " + table.getItems().size());
    }

    @Test
    @DisplayName("2. Создание новой поставки")
    void testCreateDelivery(FxRobot robot) {
        robot.clickOn("#btnAddDelivery");
        WaitForAsyncUtils.waitForFxEvents();

        Set<Node> dialogPanes = robot.lookup(".dialog-pane").queryAll();
        assertFalse(dialogPanes.isEmpty(), "Диалог создания поставки должен быть открыт");

        // Пытаемся создать поставку
        robot.clickOn("Создать");
        WaitForAsyncUtils.waitForFxEvents();

        // Проверяем статус (может быть ошибка валидации, если поля не заполнены)
        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("3. Выбор поставки и просмотр товаров")
    void testSelectDelivery(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableDeliveries").queryTableView();

        if (table.getItems().isEmpty()) {
            System.out.println("Нет поставок - пропускаем тест");
            return;
        }

        Set<Node> rows = robot.lookup(".table-row-cell").queryAll();
        if (!rows.isEmpty()) {
            robot.clickOn(rows.iterator().next());
            WaitForAsyncUtils.waitForFxEvents();

            // Проверяем панель товаров поставки
            TitledPane itemsPane = robot.lookup("#deliveryItemsPane").query();
            if (itemsPane != null && !itemsPane.isExpanded()) {
                robot.clickOn(itemsPane);
                WaitForAsyncUtils.waitForFxEvents();
            }

            TableView<?> itemsTable = robot.lookup("#tableDeliveryItems").queryTableView();
            System.out.println("Товаров в поставке: " + itemsTable.getItems().size());

            // Проверяем общую сумму
            Label totalLabel = robot.lookup("#lblDeliveryTotal").query();
            System.out.println("Общая сумма: " + totalLabel.getText());
        }
    }

    @Test
    @DisplayName("4. Проверка кнопок поставок")
    void testDeliveryButtons(FxRobot robot) {
        assertNodeExists(robot, "#btnAddDelivery", "Add Delivery");
        assertNodeExists(robot, "#btnEditDelivery", "Edit Delivery");
        assertNodeExists(robot, "#btnDeleteDelivery", "Delete Delivery");
        assertNodeExists(robot, "#cbDeliveryStatusFilter", "Status Filter");
    }

    @Test
    @DisplayName("5. Фильтрация поставок по статусу")
    void testFilterDeliveriesByStatus(FxRobot robot) {
        ComboBox<?> statusFilter = robot.lookup("#cbDeliveryStatusFilter").query();
        if (statusFilter != null && !statusFilter.getItems().isEmpty()) {
            robot.clickOn(statusFilter);
            WaitForAsyncUtils.waitForFxEvents();

            // Выбираем первый статус
            robot.type(KeyCode.DOWN);
            robot.type(KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();

            System.out.println("Фильтр применен: " + statusFilter.getValue());
        }
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}