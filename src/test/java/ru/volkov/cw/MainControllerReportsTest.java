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
@DisplayName("Вкладка Reports (Отчеты)")
class MainControllerReportsTest {

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
        robot.clickOn("Reports");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Статистические показатели отображаются")
    void testReportsStatistics(FxRobot robot) {
        // Проверяем общее количество продуктов
        Label totalProducts = robot.lookup("#lblTotalProducts").query();
        assertNotNull(totalProducts, "Метка общего количества продуктов должна существовать");
        System.out.println("Всего продуктов: " + totalProducts.getText());

        // Проверяем количество товаров с низким запасом
        Label lowStock = robot.lookup("#lblLowStock").query();
        assertNotNull(lowStock, "Метка низкого запаса должна существовать");
        System.out.println("Низкий запас: " + lowStock.getText());

        // Проверяем количество магазинов
        Label totalStores = robot.lookup("#lblTotalStores").query();
        assertNotNull(totalStores, "Метка количества магазинов должна существовать");
        System.out.println("Всего магазинов: " + totalStores.getText());
    }

    @Test
    @DisplayName("2. Кнопки быстрых действий доступны")
    void testQuickActionButtons(FxRobot robot) {
        assertNodeExists(robot, "#btnReportDelivery", "Delivery Report");
        assertNodeExists(robot, "#btnReportPrice", "Price Report");
        assertNodeExists(robot, "#btnReportInventory", "Inventory Report");
        assertNodeExists(robot, "#btnReportAudit", "Audit Report");
    }

    @Test
    @DisplayName("3. Нажатие кнопки отчета о поставках")
    void testDeliveryReportButton(FxRobot robot) {
        Button deliveryReport = robot.lookup("#btnReportDelivery").query();
        assertNotNull(deliveryReport, "Кнопка отчета о поставках должна существовать");

        robot.clickOn(deliveryReport);
        WaitForAsyncUtils.waitForFxEvents();

        System.out.println("Кнопка отчета о поставках нажата");
    }

    @Test
    @DisplayName("4. Нажатие кнопки отчета о ценах")
    void testPriceReportButton(FxRobot robot) {
        Button priceReport = robot.lookup("#btnReportPrice").query();
        assertNotNull(priceReport, "Кнопка отчета о ценах должна существовать");

        robot.clickOn(priceReport);
        WaitForAsyncUtils.waitForFxEvents();

        System.out.println("Кнопка отчета о ценах нажата");
    }

    @Test
    @DisplayName("5. Нажатие кнопки отчета об инвентаре")
    void testInventoryReportButton(FxRobot robot) {
        Button inventoryReport = robot.lookup("#btnReportInventory").query();
        assertNotNull(inventoryReport, "Кнопка отчета об инвентаре должна существовать");

        robot.clickOn(inventoryReport);
        WaitForAsyncUtils.waitForFxEvents();

        System.out.println("Кнопка отчета об инвентаре нажата");
    }

    @Test
    @DisplayName("6. Нажатие кнопки отчета аудита")
    void testAuditReportButton(FxRobot robot) {
        Button auditReport = robot.lookup("#btnReportAudit").query();
        assertNotNull(auditReport, "Кнопка отчета аудита должна существовать");

        robot.clickOn(auditReport);
        WaitForAsyncUtils.waitForFxEvents();

        System.out.println("Кнопка отчета аудита нажата");
    }

    @Test
    @DisplayName("7. Проверка панелей отчетов")
    void testReportPanes(FxRobot robot) {
        // Проверяем наличие TitledPane
        Set<TitledPane> panes = robot.lookup(".titled-pane").queryAll();
        System.out.println("Количество панелей отчетов: " + panes.size());

        for (TitledPane pane : panes) {
            System.out.println("Панель: " + pane.getText());
        }

        assertFalse(panes.isEmpty(), "Должна быть хотя бы одна панель отчетов");
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}