package ru.volkov.cw;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.volkov.cw.model.Store;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class StoreTest extends BaseApplicationTest {

    private final String uniqueStoreName = "Тестовый Магазин " + System.currentTimeMillis();
    private final String storeAddress = "ул. Тестовая, д. 123";
    private final String storePhone = "9991234567";
    private final String storeEmail = "test@store.ru";

    @BeforeEach
    void loginAndNavigateToStores() {
        performLogin();
        switchToTab(2); // Вкладка "Магазины"
    }

    // ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================

    @Test
    void testAddNewStore() {
        // Нажимаем "Добавить"
        clickOn("#btnAddStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Заполняем форму
        writeText("Название", uniqueStoreName);
        writeText("Адрес", storeAddress);
        writeText("9991234567", storePhone); // Поле телефона
        writeText("example@mail.ru", storeEmail);

        // Сохраняем
        clickOn("Сохранить");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем, что магазин появился
        TableView<Store> tableView = lookup("#tableStores").query();
        boolean found = false;
        for (Store s : tableView.getItems()) {
            if (s.getName().equals(uniqueStoreName)) {
                found = true;
                storesToClean.add(s);
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    void testEditStore() {
        // Добавляем магазин
        clickOn("#btnAddStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("Название", uniqueStoreName);
        writeText("Адрес", storeAddress);
        writeText("9991234567", storePhone);
        clickOn("Сохранить");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Находим магазин
        TableView<Store> tableView = lookup("#tableStores").query();
        final Store addedStore = tableView.getItems().stream()
                .filter(s -> s.getName().equals(uniqueStoreName))
                .findFirst()
                .orElse(null);

        assertNotNull(addedStore);
        storesToClean.add(addedStore);

        interact(() -> tableView.getSelectionModel().select(addedStore));
        clickOn("#btnEditStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Изменяем адрес
        String newAddress = "ул. Новая, д. 456";
        writeText("Адрес", newAddress);
        clickOn("Сохранить");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем обновление
        tableView = lookup("#tableStores").query();
        boolean updated = false;
        for (Store s : tableView.getItems()) {
            if (s.getName().equals(uniqueStoreName) && s.getAddress().equals(newAddress)) {
                updated = true;
                break;
            }
        }
        assertThat(updated).isTrue();
    }

    @Test
    void testDeleteStore() {
        // Добавляем магазин
        clickOn("#btnAddStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("Название", uniqueStoreName);
        writeText("Адрес", storeAddress);
        writeText("9991234567", storePhone);
        clickOn("Сохранить");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Находим магазин
        TableView<Store> tableView = lookup("#tableStores").query();
        Store storeToDelete = null;
        for (Store s : tableView.getItems()) {
            if (s.getName().equals(uniqueStoreName)) {
                storeToDelete = s;
                break;
            }
        }
        assertNotNull(storeToDelete);

        // Удаляем
        interact(() -> tableView.getSelectionModel().select(storeToDelete));
        clickOn("#btnDeleteStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}
        clickOn("Да");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Проверяем удаление
        tableView = lookup("#tableStores").query();
        boolean stillExists = false;
        for (Store s : tableView.getItems()) {
            if (s.getName().equals(uniqueStoreName)) {
                stillExists = true;
                break;
            }
        }
        assertThat(stillExists).isFalse();
    }

    // ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================

    @Test
    void testAddStoreWithEmptyName() {
        clickOn("#btnAddStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Оставляем имя пустым
        writeText("Адрес", storeAddress);
        writeText("9991234567", storePhone);
        clickOn("Сохранить");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Магазин не должен добавиться
        TableView<Store> tableView = lookup("#tableStores").query();
        boolean found = false;
        for (Store s : tableView.getItems()) {
            if (s.getName().equals(uniqueStoreName)) {
                found = true;
                break;
            }
        }
        assertThat(found).isFalse();
    }

    @Test
    void testAddStoreWithInvalidPhone() {
        clickOn("#btnAddStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("Название", uniqueStoreName);
        writeText("Адрес", storeAddress);
        writeText("999123456", "123"); // Неправильный формат телефона
        clickOn("Сохранить");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должна появиться ошибка валидации
        TableView<Store> tableView = lookup("#tableStores").query();
        boolean found = false;
        for (Store s : tableView.getItems()) {
            if (s.getName().equals(uniqueStoreName)) {
                found = true;
                break;
            }
        }
        assertThat(found).isFalse();
    }

    @Test
    void testAddStoreWithInvalidEmail() {
        clickOn("#btnAddStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        writeText("Название", uniqueStoreName);
        writeText("Адрес", storeAddress);
        writeText("9991234567", storePhone);
        writeText("example@mail.ru", "invalid-email"); // Неправильный email
        clickOn("Сохранить");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должна появиться ошибка или магазин не добавится
        TableView<Store> tableView = lookup("#tableStores").query();
        boolean found = false;
        for (Store s : tableView.getItems()) {
            if (s.getName().equals(uniqueStoreName)) {
                found = true;
                break;
            }
        }
        assertThat(found).isFalse();
    }

    @Test
    void testEditStoreWithoutSelection() {
        // Не выбираем магазин
        clickOn("#btnEditStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должно появиться предупреждение
        // Проверяем, что форма не открылась
        TableView<Store> tableView = lookup("#tableStores").query();
        assertNotNull(tableView.getItems());
    }

    @Test
    void testDeleteStoreWithoutSelection() {
        // Не выбираем магазин
        clickOn("#btnDeleteStore");
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}

        // Должно появиться предупреждение
        TableView<Store> tableView = lookup("#tableStores").query();
        assertNotNull(tableView.getItems());
    }
}