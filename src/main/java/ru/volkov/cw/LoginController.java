package ru.volkov.cw;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.LocalizationManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    // Путь к основному окну (тот же пакет)
    private static final String MAIN_VIEW = "main-view.fxml";

    @FXML
    public void initialize() {
        // Обработка Enter в поле пароля
        txtPassword.setOnAction(e -> handleLogin());

        // Убираем ошибку при начале ввода
        txtUsername.textProperty().addListener((obs, o, n) -> hideError());
        txtPassword.textProperty().addListener((obs, o, n) -> hideError());
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Базовая валидация
        if (username.isEmpty() || password.isEmpty()) {
            showError(LocalizationManager.getString("login.error.empty"));
            return;
        }

        // Блокируем кнопку на время проверки
        btnLogin.setDisable(true);
        btnLogin.setText(LocalizationManager.getString("login.checking"));

        // Проверяем подключение в отдельном потоке, чтобы не вешать UI
        new Thread(() -> {
            boolean authenticated = authenticateViaDb(username, password);

            // Возвращаемся в JavaFX Application Thread для обновления UI
            javafx.application.Platform.runLater(() -> {
                btnLogin.setDisable(false);
                btnLogin.setText(LocalizationManager.getString("login.button"));

                if (authenticated) {
                    openMainWindow();
                } else {
                    showError(LocalizationManager.getString("login.error.invalid"));
                    txtPassword.clear();
                    txtPassword.requestFocus();
                }
            });
        }).start();
    }

    /**
     * Аутентификация через механизм СУБД.
     * Пытаемся установить соединение с БД используя введенные логин/пароль.
     */
    private boolean authenticateViaDb(String username, String password) {
        String dbUrl = DatabaseConfig.getFullURL();

        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("loginTimeout", "3");

        try (Connection conn = DriverManager.getConnection(dbUrl, props)) {
            System.out.println("DB Auth successful for user: " + username);
            return true;
        } catch (SQLException e) {
            System.out.println("DB Auth failed: " + e.getSQLState() + " - " + e.getMessage());
            return false;
        }
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(MAIN_VIEW),
                    LocalizationManager.getBundle()
            );
            Parent root = loader.load();

            Stage currentStage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);

            currentStage.setScene(scene);
            currentStage.setTitle(LocalizationManager.getString("app.title"));
            currentStage.setWidth(1200);
            currentStage.setHeight(800);
            currentStage.centerOnScreen();
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка загрузки главного окна: " + e.getMessage());
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void hideError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
}