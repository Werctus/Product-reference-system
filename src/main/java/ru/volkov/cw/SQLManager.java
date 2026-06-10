package ru.volkov.cw;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SQLManager {

    private static Properties sqlProperties;
    private static final String SQL_FILE = "sql/statements.properties";

    static {
        sqlProperties = new Properties();
        loadSQLProperties();
    }

    private static void loadSQLProperties() {
        try (InputStream input = SQLManager.class.getClassLoader()
                .getResourceAsStream(SQL_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + SQL_FILE);
                return;
            }
            sqlProperties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getSQL(String key) {
        String sql = sqlProperties.getProperty(key);
        if (sql == null) {
            System.err.println("SQL key not found: " + key);
            return "";
        }
        return sql.trim();
    }

    public static void reload() {
        loadSQLProperties();
    }
}