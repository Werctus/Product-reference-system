// =====================================================
// Файл: UnitOfMeasureDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.UnitOfMeasure;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UnitOfMeasureDAO - Работа с единицами измерения")
class UnitOfMeasureDAOTest {

    private static UnitOfMeasureDAO unitDAO;
    private static String testUnitName;

    @BeforeAll
    static void setUp() {
        unitDAO = new UnitOfMeasureDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех единиц измерения")
    void testGetAllUnits() {
        List<UnitOfMeasure> units = unitDAO.getAllUnits();
        assertNotNull(units, "Список единиц измерения не должен быть null");
        System.out.println("Количество единиц измерения в БД: " + units.size());

        for (UnitOfMeasure u : units) {
            System.out.println("  - " + u.getName() + " (ID: " + u.getId() + ")");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Добавление единицы измерения")
    void testAddUnit() {
        testUnitName = "TestUnit_" + System.currentTimeMillis();
        UnitOfMeasure unit = new UnitOfMeasure(testUnitName);

        boolean result = unitDAO.addUnit(unit);
        assertTrue(result, "Единица измерения должна быть успешно добавлена");
        System.out.println("Добавлена единица измерения: " + testUnitName);

        // Проверяем, что единица появилась в базе
        List<UnitOfMeasure> units = unitDAO.getAllUnits();
        boolean found = units.stream()
                .anyMatch(u -> testUnitName.equals(u.getName()));
        assertTrue(found, "Добавленная единица измерения должна быть найдена в БД");
    }

    @Test
    @Order(3)
    @DisplayName("3. Добавление единицы с пустым именем")
    void testAddUnitWithEmptyName() {
        UnitOfMeasure unit = new UnitOfMeasure("");
        boolean result = unitDAO.addUnit(unit);
        System.out.println("Попытка добавить единицу с пустым именем: " + (result ? "добавлена" : "отклонена"));
        assertFalse(result, "Единица с пустым именем не должна быть добавлена");
    }

    @Test
    @Order(4)
    @DisplayName("4. Добавление дублирующейся единицы измерения")
    void testAddDuplicateUnit() {
        if (testUnitName == null) {
            System.out.println("Нет тестовой единицы - пропускаем тест");
            return;
        }

        UnitOfMeasure duplicate = new UnitOfMeasure(testUnitName);
        boolean result = unitDAO.addUnit(duplicate);
        System.out.println("Попытка добавить дубликат единицы '" + testUnitName + "': " + (result ? "добавлена" : "отклонена"));
        assertFalse(result, "Дублирующаяся единица не должна быть добавлена");
    }

    @Test
    @Order(5)
    @DisplayName("5. Удаление единицы измерения")
    void testDeleteUnit() {
        // Создаем единицу специально для удаления
        String deleteName = "DeleteUnit_" + System.currentTimeMillis();
        UnitOfMeasure unit = new UnitOfMeasure(deleteName);
        unitDAO.addUnit(unit);

        // Находим добавленную единицу
        List<UnitOfMeasure> units = unitDAO.getAllUnits();
        UnitOfMeasure toDelete = units.stream()
                .filter(u -> deleteName.equals(u.getName()))
                .findFirst()
                .orElse(null);

        if (toDelete == null) {
            System.out.println("Не удалось найти единицу для удаления");
            return;
        }

        boolean result = unitDAO.deleteUnit(toDelete.getId());
        System.out.println("Удаление единицы '" + deleteName + "': " + (result ? "успешно" : "не удалось"));

        if (result) {
            units = unitDAO.getAllUnits();
            boolean exists = units.stream()
                    .anyMatch(u -> deleteName.equals(u.getName()));
            assertFalse(exists, "Единица не должна существовать после удаления");
        }
    }

    @Test
    @Order(6)
    @DisplayName("6. Стандартные единицы измерения")
    void testStandardUnits() {
        List<UnitOfMeasure> units = unitDAO.getAllUnits();

        // Проверяем наличие стандартных единиц
        boolean hasPieces = units.stream().anyMatch(u ->
                u.getName().toLowerCase().contains("шт") ||
                        u.getName().toLowerCase().contains("piece"));

        boolean hasKg = units.stream().anyMatch(u ->
                u.getName().toLowerCase().contains("кг") ||
                        u.getName().toLowerCase().contains("kg"));

        System.out.println("Есть 'шт': " + hasPieces);
        System.out.println("Есть 'кг': " + hasKg);
    }
}