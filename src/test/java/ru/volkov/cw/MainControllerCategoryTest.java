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
@DisplayName("Вкладка Categories (Категории)")
class MainControllerCategoryTest {

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
        robot.clickOn("Categories");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Дерево категорий отображается")
    void testCategoryTreeVisible(FxRobot robot) {
        TreeView<?> tree = robot.lookup("#treeCategories").query();
        assertNotNull(tree, "Дерево категорий должно существовать");
        assertTrue(tree.isVisible(), "Дерево категорий должно быть видимым");

        // Проверяем корневой элемент
        TreeItem<?> root = tree.getRoot();
        System.out.println("Корневой элемент: " + (root != null ? root.getValue() : "null"));
    }

    @Test
    @DisplayName("2. Добавление новой категории")
    void testAddCategory(FxRobot robot) {
        TextField nameField = robot.lookup("#txtCategoryName").query();
        robot.clickOn(nameField);
        robot.write("New Test Category " + System.currentTimeMillis());

        robot.clickOn("#btnSaveCategory");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("3. Выбор категории в дереве")
    void testSelectCategory(FxRobot robot) {
        TreeView<?> tree = robot.lookup("#treeCategories").query();

        // Пытаемся выбрать первый элемент
        Set<Node> treeCells = robot.lookup(".tree-cell").queryAll();
        if (!treeCells.isEmpty()) {
            Node firstCell = treeCells.iterator().next();
            robot.clickOn(firstCell);
            WaitForAsyncUtils.waitForFxEvents();

            TextField nameField = robot.lookup("#txtCategoryName").query();
            System.out.println("Выбрана категория: " + nameField.getText());
        } else {
            System.out.println("Нет категорий в дереве");
        }
    }

    @Test
    @DisplayName("4. Проверка элементов управления категориями")
    void testCategoryControls(FxRobot robot) {
        assertNodeExists(robot, "#txtCategoryName", "Category Name Field");
        assertNodeExists(robot, "#cbParentCategory", "Parent Category Combo");
        assertNodeExists(robot, "#btnSaveCategory", "Save Category");
        assertNodeExists(robot, "#btnDeleteCategory", "Delete Category");
    }

    @Test
    @DisplayName("5. Выбор родительской категории")
    void testSelectParentCategory(FxRobot robot) {
        ComboBox<?> parentCombo = robot.lookup("#cbParentCategory").query();
        if (parentCombo != null && !parentCombo.getItems().isEmpty()) {
            robot.clickOn(parentCombo);
            WaitForAsyncUtils.waitForFxEvents();

            robot.type(KeyCode.DOWN);
            robot.type(KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();

            System.out.println("Выбран родитель: " + parentCombo.getValue());
        }
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}