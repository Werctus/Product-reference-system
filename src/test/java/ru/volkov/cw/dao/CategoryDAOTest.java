// =====================================================
// Файл: CategoryDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.Category;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("CategoryDAO - Работа с категориями")
class CategoryDAOTest {

    private static CategoryDAO categoryDAO;
    private static String testCategoryName;

    @BeforeAll
    static void setUp() {
        categoryDAO = new CategoryDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех категорий")
    void testGetAllCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        assertNotNull(categories, "Список категорий не должен быть null");
        System.out.println("Количество категорий в БД: " + categories.size());

        for (Category c : categories) {
            String parent = c.getParentId() != null ? " (родитель ID: " + c.getParentId() + ")" : " (главная)";
            System.out.println("  - " + c.getName() + parent);
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Получение главных категорий")
    void testGetMainCategories() {
        List<Category> mainCategories = categoryDAO.getMainCategories();
        assertNotNull(mainCategories, "Список главных категорий не должен быть null");
        System.out.println("Количество главных категорий: " + mainCategories.size());

        // Все главные категории должны иметь parentId = null или 0
        for (Category c : mainCategories) {
            assertTrue(
                    c.getParentId() == null || c.getParentId() == 0,
                    "Главная категория '" + c.getName() + "' не должна иметь родителя (parentId=" + c.getParentId() + ")"
            );
            System.out.println("  - " + c.getName());
        }
    }

    @Test
    @Order(3)
    @DisplayName("3. Добавление главной категории")
    void testAddCategory() {
        testCategoryName = "TestCategory_" + System.currentTimeMillis();
        Category category = new Category();
        category.setName(testCategoryName);
        category.setParentId(null);

        boolean result = categoryDAO.addCategory(category);
        assertTrue(result, "Категория должна быть успешно добавлена");
        System.out.println("Добавлена категория: " + testCategoryName);
    }

    @Test
    @Order(4)
    @DisplayName("4. Добавление подкатегории")
    void testAddSubCategory() {
        // Находим главную категорию
        List<Category> mainCategories = categoryDAO.getMainCategories();
        if (mainCategories.isEmpty()) {
            System.out.println("Нет главных категорий - пропускаем тест");
            return;
        }

        Category parent = mainCategories.get(0);
        String subName = "SubCategory_" + System.currentTimeMillis();

        Category subCategory = new Category();
        subCategory.setName(subName);
        subCategory.setParentId(parent.getId());

        boolean result = categoryDAO.addCategory(subCategory);
        assertTrue(result, "Подкатегория должна быть успешно добавлена");
        System.out.println("Добавлена подкатегория '" + subName + "' для родителя '" + parent.getName() + "'");
    }

    @Test
    @Order(5)
    @DisplayName("5. Получение подкатегорий")
    void testGetSubCategories() {
        List<Category> mainCategories = categoryDAO.getMainCategories();
        if (mainCategories.isEmpty()) {
            System.out.println("Нет главных категорий - пропускаем тест");
            return;
        }

        for (Category mainCat : mainCategories) {
            List<Category> subCategories = categoryDAO.getSubCategories(mainCat.getId());
            System.out.println("Подкатегорий для '" + mainCat.getName() + "': " + subCategories.size());
        }
    }

    @Test
    @Order(6)
    @DisplayName("6. Обновление категории")
    void testUpdateCategory() {
        List<Category> categories = categoryDAO.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Нет категорий - пропускаем тест");
            return;
        }

        Category category = categories.get(0);
        String originalName = category.getName();

        category.setName(originalName + " (Updated)");
        boolean result = categoryDAO.updateCategory(category);
        assertTrue(result, "Категория должна быть успешно обновлена");
        System.out.println("Категория обновлена: " + category.getName());

        // Возвращаем имя
        category.setName(originalName);
        categoryDAO.updateCategory(category);
        System.out.println("Имя категории возвращено: " + originalName);
    }

    @Test
    @Order(7)
    @DisplayName("7. Удаление категории")
    void testDeleteCategory() {
        // Создаем категорию для удаления
        String deleteName = "DeleteCat_" + System.currentTimeMillis();
        Category category = new Category();
        category.setName(deleteName);
        category.setParentId(null);
        categoryDAO.addCategory(category);

        List<Category> categories = categoryDAO.getAllCategories();
        Category toDelete = categories.stream()
                .filter(c -> deleteName.equals(c.getName()))
                .findFirst()
                .orElse(null);

        if (toDelete == null) {
            System.out.println("Не удалось найти категорию для удаления");
            return;
        }

        boolean result = categoryDAO.deleteCategory(toDelete.getId());
        System.out.println("Удаление категории '" + deleteName + "': " + (result ? "успешно" : "не удалось"));
    }
}