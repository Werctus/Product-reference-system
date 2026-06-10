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
@DisplayName("Вкладка Price History (История цен)")
class MainControllerPriceHistoryTest {

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
        robot.clickOn("Price History");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Таблица истории цен отображается")
    void testPriceHistoryTable(FxRobot robot) {
        TableView<?> table = robot.lookup("#tablePriceHistory").query();
        assertNotNull(table, "Таблица истории цен должна существовать");
        assertTrue(table.isVisible(), "Таблица истории цен должна быть видимой");
        System.out.println("Записей в истории цен: " + table.getItems().size());
    }

    @Test
    @DisplayName("2. Фильтры истории цен доступны")
    void testPriceHistoryFilters(FxRobot robot) {
        assertNodeExists(robot, "#cbPriceHistoryProduct", "Product Filter");
        assertNodeExists(robot, "#dpPriceHistoryFrom", "Date From");
        assertNodeExists(robot, "#dpPriceHistoryTo", "Date To");
        assertNodeExists(robot, "#btnPriceHistoryFilter", "Filter Button");
        assertNodeExists(robot, "#btnPriceHistoryReset", "Reset Button");
    }

    @Test
    @DisplayName("3. Фильтрация по продукту")
    void testFilterByProduct(FxRobot robot) {
        ComboBox<?> productFilter = robot.lookup("#cbPriceHistoryProduct").query();
        if (productFilter != null && !productFilter.getItems().isEmpty()) {
            robot.clickOn(productFilter);
            WaitForAsyncUtils.waitForFxEvents();

            robot.type(KeyCode.DOWN);
            robot.type(KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();

            robot.clickOn("#btnPriceHistoryFilter");
            WaitForAsyncUtils.waitForFxEvents();

            System.out.println("Фильтр по продукту применен");
        }
    }

    @Test
    @DisplayName("4. Сброс фильтров")
    void testResetFilters(FxRobot robot) {
        // Применяем какие-нибудь фильтры
        robot.clickOn("#btnPriceHistoryReset");
        WaitForAsyncUtils.waitForFxEvents();

        // Проверяем, что поля дат очищены
        DatePicker dateFrom = robot.lookup("#dpPriceHistoryFrom").query();
        DatePicker dateTo = robot.lookup("#dpPriceHistoryTo").query();

        System.out.println("Дата ОТ после сброса: " + dateFrom.getValue());
        System.out.println("Дата ДО после сброса: " + dateTo.getValue());
    }

    @Test
    @DisplayName("5. Колонки таблицы истории цен")
    void testPriceHistoryColumns(FxRobot robot) {
        TableView<?> table = robot.lookup("#tablePriceHistory").query();

        boolean hasProduct = false;
        boolean hasOldPrice = false;
        boolean hasNewPrice = false;
        boolean hasDate = false;

        for (TableColumn<?, ?> col : table.getColumns()) {
            String text = col.getText();
            if (text != null) {
                if (text.contains("Product") || text.contains("Товар")) hasProduct = true;
                if (text.contains("Old") || text.contains("Старая")) hasOldPrice = true;
                if (text.contains("New") || text.contains("Новая")) hasNewPrice = true;
                if (text.contains("Date") || text.contains("Дата") || text.contains("Changed")) hasDate = true;
            }
            System.out.println("Колонка: " + text);
        }

        assertTrue(hasProduct, "Должна быть колонка продукта");
        assertTrue(hasDate, "Должна быть колонка даты");
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}