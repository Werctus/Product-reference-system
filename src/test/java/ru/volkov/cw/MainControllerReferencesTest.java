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
@DisplayName("Вкладка References (Справочники)")
class MainControllerReferencesTest {

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
        robot.clickOn("References");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("1. Вкладка брендов активна по умолчанию")
    void testBrandsTabDefault(FxRobot robot) {
        ListView<?> brandList = robot.lookup("#listBrands").query();
        assertNotNull(brandList, "Список брендов должен существовать");
        System.out.println("Брендов в списке: " + brandList.getItems().size());
    }

    @Test
    @DisplayName("2. Добавление бренда")
    void testAddBrand(FxRobot robot) {
        TextField brandField = robot.lookup("#txtBrandName").query();
        robot.clickOn(brandField);
        robot.write("New Test Brand " + System.currentTimeMillis());

        robot.clickOn("#btnAddBrand");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("3. Переключение на вкладку Units")
    void testSwitchToUnits(FxRobot robot) {
        // Ищем вкладку Units внутри вкладки References
        robot.clickOn("Units");
        WaitForAsyncUtils.waitForFxEvents();

        ListView<?> unitList = robot.lookup("#listUnits").query();
        assertNotNull(unitList, "Список единиц измерения должен существовать");
        System.out.println("Единиц измерения: " + unitList.getItems().size());
    }

    @Test
    @DisplayName("4. Добавление единицы измерения")
    void testAddUnit(FxRobot robot) {
        robot.clickOn("Units");
        WaitForAsyncUtils.waitForFxEvents();

        TextField unitField = robot.lookup("#txtUnitName").query();
        robot.clickOn(unitField);
        robot.write("New Test Unit " + System.currentTimeMillis());

        robot.clickOn("#btnAddUnit");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusBar = robot.lookup("#lblStatusBar").query();
        System.out.println("Статус: " + statusBar.getText());
    }

    @Test
    @DisplayName("5. Удаление бренда")
    void testDeleteBrand(FxRobot robot) {
        ListView<?> brandList = robot.lookup("#listBrands").query();

        if (brandList.getItems().isEmpty()) {
            System.out.println("Нет брендов - пропускаем тест");
            return;
        }

        // Выбираем первый бренд
        Set<Node> cells = robot.lookup(".list-cell").queryAll();
        if (!cells.isEmpty()) {
            robot.clickOn(cells.iterator().next());
            WaitForAsyncUtils.waitForFxEvents();

            robot.clickOn("#btnDeleteBrand");
            WaitForAsyncUtils.waitForFxEvents();

            // Проверяем диалог подтверждения
            Set<Node> dialogPanes = robot.lookup(".dialog-pane").queryAll();
            if (!dialogPanes.isEmpty()) {
                System.out.println("Диалог подтверждения открыт");
                // Закрываем диалог
                robot.clickOn("Cancel");
                WaitForAsyncUtils.waitForFxEvents();
            }
        }
    }

    @Test
    @DisplayName("6. Проверка элементов интерфейса")
    void testReferencesUIElements(FxRobot robot) {
        assertNodeExists(robot, "#txtBrandName", "Brand Name Field");
        assertNodeExists(robot, "#btnAddBrand", "Add Brand Button");
        assertNodeExists(robot, "#btnDeleteBrand", "Delete Brand Button");
        assertNodeExists(robot, "#listBrands", "Brands List");

        robot.clickOn("Units");
        WaitForAsyncUtils.waitForFxEvents();

        assertNodeExists(robot, "#txtUnitName", "Unit Name Field");
        assertNodeExists(robot, "#btnAddUnit", "Add Unit Button");
        assertNodeExists(robot, "#btnDeleteUnit", "Delete Unit Button");
        assertNodeExists(robot, "#listUnits", "Units List");
    }

    private void assertNodeExists(FxRobot robot, String selector, String description) {
        Set<Node> nodes = robot.lookup(selector).queryAll();
        assertFalse(nodes.isEmpty(), description + " (" + selector + ") должен существовать");
    }
}