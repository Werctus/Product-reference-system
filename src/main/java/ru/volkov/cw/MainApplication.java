package ru.volkov.cw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.LocalizationManager;
import util.LoggingUtil;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainApplication extends Application {

    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());

    private static Stage primaryStage;
    private static ResourceBundle bundle;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Locale currentLocale = LocalizationManager.getCurrentLocale();
        bundle = LocalizationManager.getBundle();

        logger.info("Starting application with locale: " + currentLocale);
        logger.info("Loaded resources: " + bundle.getLocale());

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("login-view.fxml"),
                bundle
        );

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle(bundle.getString("login.window.title"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();

        logger.info("Login window displayed successfully");
    }

    /**
     * Возвращает главную сцену приложения.
     * Используется LoginController для замены сцены после успешной авторизации.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void main(String[] args) {
        LoggingUtil.setupLogging();

        logger.info("Application initialization started");
        logger.info("Default locale: " + Locale.getDefault());

        launch();
    }
}