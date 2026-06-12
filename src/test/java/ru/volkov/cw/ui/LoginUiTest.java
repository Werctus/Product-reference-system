package ru.volkov.cw.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import util.LocalizationManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тест для проверки успешной авторизации и загрузки главного окна.
 */
@ExtendWith(ApplicationExtension.class)
public class LoginUiTest {

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ru/volkov/cw/login-view.fxml"),
                LocalizationManager.getBundle()
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 350);
        stage.setScene(scene);
        stage.setTitle("Login Test");
        stage.show();
    }

    /**
     * Тестовый контроллер с замоканной аутентификацией
     */
    public static class TestLoginController extends ru.volkov.cw.controller.LoginController {
        @Override
        protected boolean authenticateViaDb(String username, String password) {
            // В тестах всегда возвращаем успех
            System.out.println("🔧 Mock authentication for user: " + username);
            return true;
        }
    }

    @Test
    public void opensMainWindowAfterLogin(FxRobot robot) throws Exception {
        robot.clickOn("#txtUsername").write("app_admin");
        robot.clickOn("#txtPassword").write("app_admin");

        robot.clickOn("#btnLogin");

        Thread.sleep(3000);

        TabPane tabPane = robot.lookup("#mainTabPane").tryQueryAs(TabPane.class).orElse(null);
        assertNotNull(tabPane, "Главное окно с TabPane должно загрузиться после авторизации");

        assertNotNull(robot.lookup("#lblStatusBar").tryQuery().orElse(null),
                "Статус-бар должен присутствовать в главном окне");

        System.out.println("✅ Тест 1 пройден: успешная авторизация и загрузка главного окна");
    }
}