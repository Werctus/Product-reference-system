// src/test/java/ru/volkov/cw/MainApplicationTest.java
package ru.volkov.cw;

import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import javafx.scene.Node;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class MainApplicationTest {

    private Stage primaryStage;

    @Start
    public void start(Stage stage) {
        this.primaryStage = stage;
        try {
            new MainApplication().start(stage);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось запустить приложение", e);
        }
    }

    @BeforeEach
    void setUp(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    @DisplayName("Приложение должно запускаться успешно")
    void testApplicationStarts(FxRobot robot) {
        // Проверяем, что окно видимо
        assertTrue(primaryStage.isShowing(), "Окно приложения должно быть видимым");

        // Проверяем заголовок
        String title = primaryStage.getTitle();
        assertNotNull(title, "Заголовок окна не должен быть null");
        assertFalse(title.isEmpty(), "Заголовок окна не должен быть пустым");
        System.out.println("Заголовок окна: " + title);

        // Проверяем наличие главного TabPane
        TabPane tabPane = robot.lookup("#mainTabPane").query();
        assertNotNull(tabPane, "Main TabPane должен существовать");
        assertTrue(tabPane.isVisible(), "Main TabPane должен быть видимым");

        // Проверяем наличие статус-бара
        Label statusBar = robot.lookup("#lblStatusBar").query();
        assertNotNull(statusBar, "Статус-бар должен существовать");
        assertTrue(statusBar.isVisible(), "Статус-бар должен быть видимым");
        System.out.println("Статус-бар: " + statusBar.getText());

        // Проверяем наличие ComboBox языка
        ComboBox<?> languageCombo = robot.lookup("#cbLanguage").query();
        assertNotNull(languageCombo, "ComboBox языка должен существовать");
        assertFalse(languageCombo.getItems().isEmpty(), "ComboBox языка должен содержать элементы");
        System.out.println("Доступные языки: " + languageCombo.getItems());
    }

    @Test
    @DisplayName("Все вкладки должны быть доступны")
    void testAllTabsPresent(FxRobot robot) {
        TabPane tabPane = robot.lookup("#mainTabPane").query();
        assertNotNull(tabPane, "TabPane должен существовать");

        int tabCount = tabPane.getTabs().size();
        System.out.println("Количество вкладок: " + tabCount);
        assertTrue(tabCount >= 8, "Должно быть минимум 8 вкладок, найдено: " + tabCount);

        // Проверяем наличие всех вкладок
        String[] expectedTabs = {
                "Products", "Categories", "Stores", "Companies",
                "Deliveries", "Price History", "Audit Log",
                "References", "Reports"
        };

        for (String expectedTab : expectedTabs) {
            boolean tabFound = tabPane.getTabs().stream()
                    .anyMatch(tab -> expectedTab.equals(tab.getText()));

            if (!tabFound) {
                System.out.println("Вкладка не найдена: " + expectedTab);
                // Проверяем, может быть название загружается из ресурсов
                tabFound = tabPane.getTabs().stream()
                        .anyMatch(tab -> tab.getText() != null &&
                                tab.getText().toLowerCase().contains(expectedTab.toLowerCase()));
            }

            assertTrue(tabFound, "Вкладка '" + expectedTab + "' должна существовать");
        }

        System.out.println("Все вкладки присутствуют");
    }

    @Test
    @DisplayName("Переключение между вкладками")
    void testSwitchTabs(FxRobot robot) {
        TabPane tabPane = robot.lookup("#mainTabPane").query();

        // Переключаемся на вкладку Products
        robot.clickOn("Products");
        WaitForAsyncUtils.waitForFxEvents();

        // Проверяем, что отображается таблица продуктов
        Set<Node> productTable = robot.lookup("#tableProducts").queryAll();
        assertFalse(productTable.isEmpty(), "Таблица продуктов должна быть видна");

        // Переключаемся на вкладку Stores
        robot.clickOn("Stores");
        WaitForAsyncUtils.waitForFxEvents();

        Set<Node> storeTable = robot.lookup("#tableStores").queryAll();
        assertFalse(storeTable.isEmpty(), "Таблица магазинов должна быть видна");

        // Переключаемся на вкладку Companies
        robot.clickOn("Companies");
        WaitForAsyncUtils.waitForFxEvents();

        Set<Node> companyTable = robot.lookup("#tableCompanies").queryAll();
        assertFalse(companyTable.isEmpty(), "Таблица компаний должна быть видна");

        System.out.println("Переключение между вкладками работает корректно");
    }

    @Test
    @DisplayName("Переключение языка")
    void testLanguageSwitch(FxRobot robot) {
        // Находим ComboBox языка
        ComboBox<?> languageCombo = robot.lookup("#cbLanguage").query();
        assertNotNull(languageCombo, "ComboBox языка должен существовать");

        String initialLanguage = languageCombo.getValue() != null ?
                languageCombo.getValue().toString() : "unknown";
        System.out.println("Начальный язык: " + initialLanguage);

        // Открываем выпадающий список
        robot.clickOn(languageCombo);
        WaitForAsyncUtils.waitForFxEvents();

        // Проверяем, что список открылся
        assertTrue(languageCombo.isShowing(), "Выпадающий список должен быть открыт");

        // Выбираем английский (индекс 1 или текст "EN English")
        if (languageCombo.getItems().size() > 1) {
            // Закрываем список, нажимая на элемент
            Object englishItem = languageCombo.getItems().get(1);
            robot.clickOn(englishItem.toString());
            WaitForAsyncUtils.waitForFxEvents();

            String newLanguage = languageCombo.getValue().toString();
            System.out.println("Новый язык: " + newLanguage);
            assertTrue(newLanguage.contains("EN"), "Язык должен быть английским");
        }
    }

    @Test
    @DisplayName("Проверка видимости всех кнопок навигации")
    void testNavigationButtons(FxRobot robot) {
        // Переключаемся на Products
        robot.clickOn("Products");
        WaitForAsyncUtils.waitForFxEvents();

        // Проверяем кнопки продуктов
        assertButtonExists(robot, "#btnAddProduct", "Add Product button");
        assertButtonExists(robot, "#btnEditProduct", "Edit Product button");
        assertButtonExists(robot, "#btnDeleteProduct", "Delete Product button");
        assertButtonExists(robot, "#btnRefreshProduct", "Refresh Product button");

        // Переключаемся на Companies
        robot.clickOn("Companies");
        WaitForAsyncUtils.waitForFxEvents();

        assertButtonExists(robot, "#btnAddCompany", "Add Company button");
        assertButtonExists(robot, "#btnEditCompany", "Edit Company button");
        assertButtonExists(robot, "#btnDeleteCompany", "Delete Company button");

        System.out.println("Все кнопки навигации присутствуют");
    }

    private void assertButtonExists(FxRobot robot, String selector, String description) {
        Set<Node> buttons = robot.lookup(selector).queryAll();
        assertFalse(buttons.isEmpty(), description + " должен существовать");
    }
}