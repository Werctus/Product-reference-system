package util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.logging.Logger;

public class LocalizationManager {

    private static final Logger logger = Logger.getLogger(LocalizationManager.class.getName());
    private static final String BUNDLE_NAME = "ru.volkov.cw.main";
    private static final String PREFS_LANGUAGE = "app.language";

    private static ResourceBundle resourceBundle;
    private static Locale currentLocale;
    private static Preferences prefs;

    static {
        prefs = Preferences.userNodeForPackage(LocalizationManager.class);
        loadLocale();
    }

    private static void loadLocale() {
        try {
            String savedLocale = prefs.get(PREFS_LANGUAGE, null);

            if (savedLocale != null && !savedLocale.isEmpty()) {
                String[] parts = savedLocale.split("_");
                currentLocale = parts.length == 2
                        ? new Locale(parts[0], parts[1])
                        : new Locale(parts[0]);
            } else {
                // По умолчанию русский
                currentLocale = new Locale("ru", "RU");
                prefs.put(PREFS_LANGUAGE, "ru_RU");
            }

            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
            logger.info("Loaded resources for locale: " + resourceBundle.getLocale());

        } catch (Exception e) {
            logger.severe("Failed to load resource bundle: " + e.getMessage());
            // Fallback на базовый файл (английский)
            currentLocale = Locale.ENGLISH;
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
        }
    }

    public static Locale getCurrentLocale() { return currentLocale; }

    // Метод для смены языка
    public static void setLocale(String language, String country) {
        currentLocale = new Locale(language, country);
        Locale.setDefault(currentLocale);
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
        // ОБЯЗАТЕЛЬНО сохраняем выбор в Preferences
        prefs.put(PREFS_LANGUAGE, language + "_" + country);
        logger.info("Locale changed and saved to: " + currentLocale);
    }

    public static String getString(String key) {
        try { return resourceBundle.getString(key); }
        catch (Exception e) { return "!" + key + "!"; }
    }

    public static ResourceBundle getBundle() { return resourceBundle; }

    public static void reload() {
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    }
}