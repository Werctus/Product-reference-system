// =====================================================
// Файл: StoreDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.Store;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("StoreDAO - Работа с магазинами")
class StoreDAOTest {

    private static StoreDAO storeDAO;
    private static String testPhone;

    @BeforeAll
    static void setUp() {
        storeDAO = new StoreDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех магазинов")
    void testGetAllStores() {
        List<Store> stores = storeDAO.getAllStores();
        assertNotNull(stores, "Список магазинов не должен быть null");
        System.out.println("Количество магазинов в БД: " + stores.size());

        for (Store s : stores) {
            System.out.println("  - " + s.getName() + " (Адрес: " + s.getAddress() + ")");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Добавление магазина")
    void testAddStore() {
        testPhone = "+7999" + String.valueOf(System.currentTimeMillis()).substring(0, 7);

        Store store = new Store();
        store.setName("Test Store " + System.currentTimeMillis());
        store.setAddress("Test Address, 123");
        store.setPhone(testPhone);
        store.setEmail("test" + System.currentTimeMillis() + "@example.com");

        boolean result = storeDAO.addStore(store);
        assertTrue(result, "Магазин должен быть успешно добавлен");
        System.out.println("Добавлен магазин: " + store.getName() + " (Телефон: " + testPhone + ")");

        // Проверяем, что магазин появился в базе
        List<Store> stores = storeDAO.getAllStores();
        boolean found = stores.stream()
                .anyMatch(s -> testPhone.equals(s.getPhone()));
        assertTrue(found, "Добавленный магазин должен быть найден в БД");
    }

    @Test
    @Order(3)
    @DisplayName("3. Обновление магазина")
    void testUpdateStore() {
        List<Store> stores = storeDAO.getAllStores();
        if (stores.isEmpty()) {
            System.out.println("Нет магазинов для обновления - пропускаем тест");
            return;
        }

        Store store = stores.get(0);
        String originalName = store.getName();
        String originalEmail = store.getEmail();

        // Изменяем данные
        store.setName(originalName + " (Updated)");
        store.setEmail("updated" + System.currentTimeMillis() + "@example.com");

        boolean result = storeDAO.updateStore(store);
        assertTrue(result, "Магазин должен быть успешно обновлен");
        System.out.println("Магазин обновлен: " + store.getName());

        // Возвращаем исходные данные
        store.setName(originalName);
        store.setEmail(originalEmail);
        storeDAO.updateStore(store);
        System.out.println("Данные магазина возвращены к исходным");
    }

    @Test
    @Order(4)
    @DisplayName("4. Добавление магазина без email")
    void testAddStoreWithoutEmail() {
        Store store = new Store();
        store.setName("Store Without Email " + System.currentTimeMillis());
        store.setAddress("No Email Address");
        store.setPhone("+7999" + String.valueOf(System.currentTimeMillis()).substring(0, 7));
        store.setEmail(null);

        boolean result = storeDAO.addStore(store);
        assertTrue(result, "Магазин без email должен быть добавлен");
        System.out.println("Добавлен магазин без email: " + store.getName());
    }

    @Test
    @Order(5)
    @DisplayName("5. Удаление магазина")
    void testDeleteStore() {
        // Создаем магазин специально для удаления
        String deletePhone = "+7999" + String.valueOf(System.currentTimeMillis() + 1).substring(0, 7);

        Store store = new Store();
        store.setName("DeleteStore " + System.currentTimeMillis());
        store.setAddress("Delete Address");
        store.setPhone(deletePhone);

        storeDAO.addStore(store);

        // Находим добавленный магазин
        List<Store> stores = storeDAO.getAllStores();
        Store toDelete = stores.stream()
                .filter(s -> deletePhone.equals(s.getPhone()))
                .findFirst()
                .orElse(null);

        assertNotNull(toDelete, "Магазин для удаления должен быть найден");

        boolean result = storeDAO.deleteStore(toDelete.getId());
        assertTrue(result, "Магазин должен быть успешно удален");
        System.out.println("Магазин удален: " + toDelete.getName() + " (ID: " + toDelete.getId() + ")");

        // Проверяем, что магазин действительно удален
        stores = storeDAO.getAllStores();
        boolean exists = stores.stream()
                .anyMatch(s -> deletePhone.equals(s.getPhone()));
        assertFalse(exists, "Магазин не должен существовать после удаления");
    }
}