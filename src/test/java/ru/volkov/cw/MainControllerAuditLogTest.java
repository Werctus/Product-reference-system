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
@DisplayName("Вкладка Audit Log (Журнал аудита)")
class MainControllerAuditLogTest {

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
        robot.clickOn("Audit Log");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Таблица аудита загружается")
    void testAuditLogTableLoads(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableAuditLog").query();
        assertNotNull(table, "Таблица аудита должна существовать");
        assertTrue(table.isVisible(), "Таблица аудита должна быть видимой");
        System.out.println("Записей в аудите: " + table.getItems().size());
    }

    @Test
    @DisplayName("2. Фильтры аудита доступны")
    void testAuditFiltersAvailable(FxRobot robot) {
        assertNodeExists(robot, "#cbAuditTable", "Table Filter");
        assertNodeExists(robot, "#cbAuditAction", "Action Filter");
        assertNodeExists(robot, "#dpAuditFrom", "Date From");
        assertNodeExists(robot, "#dpAuditTo", "Date To");
        assertNodeExists(robot, "#btnAuditFilter", "Filter Button");
        assertNodeExists(robot, "#btnAuditClear", "Clear Button");
    }

    @Test
    @DisplayName("3. Фильтрация по таблице")
    void testFilterByTable(FxRobot robot) {
        ComboBox<?> tableFilter = robot.lookup("#cbAuditTable").query();
        if (tableFilter != null && !tableFilter.getItems().isEmpty()) {
            robot.clickOn(tableFilter);
            WaitForAsyncUtils.waitForFxEvents();

            robot.type(KeyCode.DOWN);
            robot.type(KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();

            robot.clickOn("#btnAuditFilter");
            WaitForAsyncUtils.waitForFxEvents();

            System.out.println("Фильтр по таблице применен: " + tableFilter.getValue());
        }
    }

    @Test
    @DisplayName("4. Фильтрация по действию")
    void testFilterByAction(FxRobot robot) {
        ComboBox<?> actionFilter = robot.lookup("#cbAuditAction").query();
        if (actionFilter != null && !actionFilter.getItems().isEmpty()) {
            robot.clickOn(actionFilter);
            WaitForAsyncUtils.waitForFxEvents();

            robot.type(KeyCode.DOWN);
            robot.type(KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();

            robot.clickOn("#btnAuditFilter");
            WaitForAsyncUtils.waitForFxEvents();

            System.out.println("Фильтр по действию применен: " + actionFilter.getValue());
        }
    }

    @Test
    @DisplayName("5. Колонки таблицы аудита")
    void testAuditTableColumns(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableAuditLog").query();
        System.out.println("Количество колонок: " + table.getColumns().size());

        for (TableColumn<?, ?> col : table.getColumns()) {
            System.out.println("Колонка: " + col.getText());
        }

        assertTrue(table.getColumns().size() >= 6, "Должно быть минимум 6 колонок");
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}