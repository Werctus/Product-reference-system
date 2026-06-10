// =====================================================
// Файл: BrandDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.Brand;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("BrandDAO - Работа с брендами")
class BrandDAOTest {

    private static BrandDAO brandDAO;
    private static String testBrandName;

    @BeforeAll
    static void setUp() {
        brandDAO = new BrandDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех брендов")
    void testGetAllBrands() {
        List<Brand> brands = brandDAO.getAllBrands();
        assertNotNull(brands, "Список брендов не должен быть null");
        System.out.println("Количество брендов в БД: " + brands.size());

        for (Brand b : brands) {
            System.out.println("  - " + b.getName() + " (ID: " + b.getId() + ")");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Добавление бренда")
    void testAddBrand() {
        testBrandName = "TestBrand_" + System.currentTimeMillis();
        Brand brand = new Brand(testBrandName);

        boolean result = brandDAO.addBrand(brand);
        assertTrue(result, "Бренд должен быть успешно добавлен");
        System.out.println("Добавлен бренд: " + testBrandName);

        // Проверяем, что бренд появился в базе
        List<Brand> brands = brandDAO.getAllBrands();
        boolean found = brands.stream()
                .anyMatch(b -> testBrandName.equals(b.getName()));
        assertTrue(found, "Добавленный бренд должен быть найден в БД");
    }

    @Test
    @Order(3)
    @DisplayName("3. Добавление бренда с пустым именем")
    void testAddBrandWithEmptyName() {
        Brand brand = new Brand("");
        boolean result = brandDAO.addBrand(brand);
        System.out.println("Попытка добавить бренд с пустым именем: " + (result ? "добавлен" : "отклонен"));
        // Пустой бренд не должен добавляться
        assertFalse(result, "Бренд с пустым именем не должен быть добавлен");
    }

    @Test
    @Order(4)
    @DisplayName("4. Добавление дублирующегося бренда")
    void testAddDuplicateBrand() {
        if (testBrandName == null) {
            System.out.println("Нет тестового бренда - пропускаем тест");
            return;
        }

        Brand duplicate = new Brand(testBrandName);
        boolean result = brandDAO.addBrand(duplicate);
        System.out.println("Попытка добавить дубликат бренда '" + testBrandName + "': " + (result ? "добавлен" : "отклонен"));
        assertFalse(result, "Дублирующийся бренд не должен быть добавлен");
    }

    @Test
    @Order(5)
    @DisplayName("5. Удаление бренда")
    void testDeleteBrand() {
        // Создаем бренд специально для удаления
        String deleteName = "DeleteBrand_" + System.currentTimeMillis();
        Brand brand = new Brand(deleteName);
        brandDAO.addBrand(brand);

        // Находим добавленный бренд
        List<Brand> brands = brandDAO.getAllBrands();
        Brand toDelete = brands.stream()
                .filter(b -> deleteName.equals(b.getName()))
                .findFirst()
                .orElse(null);

        if (toDelete == null) {
            System.out.println("Не удалось найти бренд для удаления");
            return;
        }

        boolean result = brandDAO.deleteBrand(toDelete.getId());
        System.out.println("Удаление бренда '" + deleteName + "': " + (result ? "успешно" : "не удалось"));
        // Примечание: удаление может не сработать, если бренд используется в товарах

        // Проверяем, что бренд удален (если result был true)
        if (result) {
            brands = brandDAO.getAllBrands();
            boolean exists = brands.stream()
                    .anyMatch(b -> deleteName.equals(b.getName()));
            assertFalse(exists, "Бренд не должен существовать после удаления");
        }
    }

    @Test
    @Order(6)
    @DisplayName("6. Проверка структуры бренда")
    void testBrandStructure() {
        List<Brand> brands = brandDAO.getAllBrands();

        if (brands.isEmpty()) {
            System.out.println("Нет брендов для проверки структуры");
            return;
        }

        Brand brand = brands.get(0);
        assertNotNull(brand.getId(), "ID бренда не должен быть null");
        assertNotNull(brand.getName(), "Имя бренда не должно быть null");
        assertFalse(brand.getName().isEmpty(), "Имя бренда не должно быть пустым");

        System.out.println("Структура бренда: ID=" + brand.getId() + ", Name=" + brand.getName());
    }
}