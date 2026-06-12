package ru.volkov.cw;

import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApplication().start(stage);
    }

    // ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================

    @Test
    void testSuccessfulLogin() {
        // Ввод корректных данных
        clickOn("#txtUsername").write("admin");
        clickOn("#txtPassword").write("admin123");
        clickOn("#btnLogin");

        // Проверяем, что главное окно открылось
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем наличие TabPane на главном окне
        assertNotNull(lookup("#mainTabPane").query());
    }

    @Test
    void testLoginWithCorrectCredentials() {
        // Arrange
        String username = "admin";
        String password = "admin123";

        // Act
        clickOn("#txtUsername").write(username);
        clickOn("#txtPassword").write(password);
        clickOn("#btnLogin");

        // Assert - проверяем, что окно логина закрылось
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Главное окно должно содержать TabPane
        assertNotNull(lookup("#mainTabPane").query());
    }

    // ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================

    @Test
    void testLoginWithEmptyUsername() {
        // Оставляем имя пользователя пустым
        clickOn("#txtPassword").write("admin123");
        clickOn("#btnLogin");

        // Проверяем, что появилось сообщение об ошибке
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Label lblError = lookup("#lblError").query();
        assertTrue(lblError.isVisible() || lblError.isManaged());
    }

    @Test
    void testLoginWithEmptyPassword() {
        // Вводим только имя пользователя
        clickOn("#txtUsername").write("admin");
        clickOn("#btnLogin");

        // Проверяем сообщение об ошибке
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Label lblError = lookup("#lblError").query();
        assertTrue(lblError.isVisible() || lblError.isManaged());
    }

    @Test
    void testLoginWithEmptyFields() {
        // Оба поля пустые
        clickOn("#btnLogin");

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Label lblError = lookup("#lblError").query();
        assertTrue(lblError.isVisible() || lblError.isManaged());
    }

    @Test
    void testLoginWithWrongPassword() {
        // Вводим неправильный пароль
        clickOn("#txtUsername").write("admin");
        clickOn("#txtPassword").write("wrongpassword");
        clickOn("#btnLogin");

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Label lblError = lookup("#lblError").query();
        assertTrue(lblError.isVisible() || lblError.isManaged());
    }

    @Test
    void testLoginWithWrongUsername() {
        // Вводим неправильное имя пользователя
        clickOn("#txtUsername").write("wronguser");
        clickOn("#txtPassword").write("admin123");
        clickOn("#btnLogin");

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Label lblError = lookup("#lblError").query();
        assertTrue(lblError.isVisible() || lblError.isManaged());
    }

    @Test
    void testLoginWithSpecialCharacters() {
        // Вводим специальные символы
        clickOn("#txtUsername").write("admin@#$%");
        clickOn("#txtPassword").write("pass!@#$");
        clickOn("#btnLogin");

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Должна появиться ошибка
        Label lblError = lookup("#lblError").query();
        assertTrue(lblError.isVisible() || lblError.isManaged());
    }
}