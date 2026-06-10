package ru.volkov.cw;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static Properties dbProperties;
    private static final String DB_FILE = "database.properties";

    static {
        dbProperties = new Properties();
        loadDBProperties();
    }

    private static void loadDBProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream(DB_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + DB_FILE);
                return;
            }
            dbProperties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return dbProperties.getProperty(key);
    }

    public static Connection getConnection() throws SQLException {
        String driver = getProperty("db.driver");
        String url = getProperty("db.url") + getProperty("db.name");
        String user = getProperty("db.user");
        String password = getProperty("db.password");

        // Проверяем, что свойства успешно загрузились
        if (driver == null || url == null || user == null || password == null) {
            throw new SQLException("Не удалось загрузить настройки БД из файла " + DB_FILE +
                    ". Проверьте, скопирован ли файл в target/classes.");
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Драйвер БД не найден: " + driver + ". Проверьте зависимости в pom.xml.", e);
        }

        return DriverManager.getConnection(url, user, password);
    }

    public static String getFullURL() {
        return getProperty("db.url") + getProperty("db.name");
    }
}
