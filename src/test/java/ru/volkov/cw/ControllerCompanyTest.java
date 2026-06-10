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
@DisplayName("Вкладка Companies (Фирмы)")
class MainControllerCompanyTest {

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
        robot.clickOn("Companies");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Добавление компании")
    void testAddCompany(FxRobot robot) {
        robot.clickOn("#btnAddCompany");
        WaitForAsyncUtils.waitForFxEvents();

        Set<Node> dialogPanes = robot.lookup(".dialog-pane").queryAll();
        assertFalse(dialogPanes.isEmpty(), "Диалог должен быть открыт");

        Set<TextField> textFields = robot.lookup(".text-field").queryAll();
        TextField[] fields = textFields.toArray(new TextField[0]);

        robot.clickOn(fields[0]).write("Test Company");
        robot.clickOn(fields[1]).write("1234567890");
        robot.clickOn(fields[2]).write("4.50");
        robot.clickOn(fields[3]).write("9991234567");
        robot.clickOn(fields[4]).write("Test Address");

        robot.clickOn("Сохранить");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("2. Редактирование компании")
    void testEditCompany(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableCompanies").queryTableView();

        if (table.getItems().isEmpty()) {
            System.out.println("Нет компаний - пропускаем тест");
            return;
        }

        Set<Node> rows = robot.lookup(".table-row-cell").queryAll();
        if (!rows.isEmpty()) {
            robot.clickOn(rows.iterator().next());
            WaitForAsyncUtils.waitForFxEvents();

            robot.clickOn("#btnEditCompany");
            WaitForAsyncUtils.waitForFxEvents();

            Set<TextField> textFields = robot.lookup(".text-field").queryAll();
            TextField[] fields = textFields.toArray(new TextField[0]);

            robot.doubleClickOn(fields[0]);
            robot.write("Updated Company");

            robot.clickOn("Сохранить");
            WaitForAsyncUtils.waitForFxEvents();
        }
    }

    @Test
    @DisplayName("3. Валидация ИНН (короткий)")
    void testCompanyINNValidation(FxRobot robot) {
        robot.clickOn("#btnAddCompany");
        WaitForAsyncUtils.waitForFxEvents();

        Set<TextField> textFields = robot.lookup(".text-field").queryAll();
        TextField[] fields = textFields.toArray(new TextField[0]);

        robot.clickOn(fields[0]).write("Test Company");
        robot.clickOn(fields[1]).write("123"); // Некорректный ИНН
        robot.clickOn(fields[3]).write("9991234567");
        robot.clickOn(fields[4]).write("Test Address");

        robot.clickOn("Сохранить");
        WaitForAsyncUtils.waitForFxEvents();

        String style = fields[1].getStyle();
        System.out.println("Стиль поля ИНН: " + style);
        assertTrue(style.contains("red"), "Поле ИНН должно быть красным");
    }

    @Test
    @DisplayName("4. Валидация телефона (короткий)")
    void testCompanyPhoneValidation(FxRobot robot) {
        robot.clickOn("#btnAddCompany");
        WaitForAsyncUtils.waitForFxEvents();

        Set<TextField> textFields = robot.lookup(".text-field").queryAll();
        TextField[] fields = textFields.toArray(new TextField[0]);

        robot.clickOn(fields[0]).write("Test Company");
        robot.clickOn(fields[1]).write("1234567890");
        robot.clickOn(fields[3]).write("12"); // Некорректный телефон
        robot.clickOn(fields[4]).write("Test Address");

        robot.clickOn("Сохранить");
        WaitForAsyncUtils.waitForFxEvents();

        String style = fields[3].getStyle();
        System.out.println("Стиль поля телефона: " + style);
        assertTrue(style.contains("red"), "Поле телефона должно быть красным");
    }

    @Test
    @DisplayName("5. Удаление компании")
    void testDeleteCompany(FxRobot robot) {
        TableView<?> table = robot.lookup("#tableCompanies").queryTableView();

        if (table.getItems().isEmpty()) {
            System.out.println("Нет компаний - пропускаем тест");
            return;
        }

        Set<Node> rows = robot.lookup(".table-row-cell").queryAll();
        if (!rows.isEmpty()) {
            robot.clickOn(rows.iterator().next());
            WaitForAsyncUtils.waitForFxEvents();

            robot.clickOn("#btnDeleteCompany");
            WaitForAsyncUtils.waitForFxEvents();

            // Ищем кнопку подтверждения
            Set<Button> buttons = robot.lookup(".button").queryAll();
            for (Button button : buttons) {
                String text = button.getText();
                if (text != null && (text.equals("OK") || text.equals("Yes") || text.equals("Да"))) {
                    robot.clickOn(button);
                    break;
                }
            }
            WaitForAsyncUtils.waitForFxEvents();
        }
    }

    @Test
    @DisplayName("6. Отмена добавления компании")
    void testCancelAddCompany(FxRobot robot) {
        robot.clickOn("#btnAddCompany");
        WaitForAsyncUtils.waitForFxEvents();

        Set<TextField> textFields = robot.lookup(".text-field").queryAll();
        TextField[] fields = textFields.toArray(new TextField[0]);
        robot.clickOn(fields[0]).write("Cancel Company");

        robot.clickOn("Cancel");
        WaitForAsyncUtils.waitForFxEvents();

        Set<Node> dialogPanes = robot.lookup(".dialog-pane").queryAll();
        boolean dialogClosed = dialogPanes.isEmpty() ||
                dialogPanes.stream().noneMatch(Node::isVisible);
        assertTrue(dialogClosed, "Диалог должен быть закрыт");
    }
}