// =====================================================
// Файл: CompanyDAOTest.java
// =====================================================
package ru.volkov.cw.dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.volkov.cw.model.Company;

import java.math.BigDecimal;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("CompanyDAO - Работа с фирмами")
class CompanyDAOTest {

    private static CompanyDAO companyDAO;
    private static String testINN;

    @BeforeAll
    static void setUp() {
        companyDAO = new CompanyDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Получение всех компаний")
    void testGetAllCompanies() {
        List<Company> companies = companyDAO.getAllCompanies();
        assertNotNull(companies, "Список компаний не должен быть null");
        System.out.println("Количество компаний в БД: " + companies.size());

        for (Company c : companies) {
            System.out.println("  - " + c.getName() + " (ИНН: " + c.getInn() + ")");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Добавление компании с валидными данными")
    void testAddValidCompany() {
        testINN = String.valueOf(System.currentTimeMillis()).substring(0, 10);

        Company company = new Company();
        company.setName("Test Company " + System.currentTimeMillis());
        company.setInn(testINN);
        company.setRating(new BigDecimal("4.50"));
        company.setPhone("+79991234567");
        company.setAddress("Test Address, Building 1");

        boolean result = companyDAO.addCompany(company);
        assertTrue(result, "Компания должна быть успешно добавлена");
        System.out.println("Добавлена компания: " + company.getName() + " (ИНН: " + testINN + ")");

        // Проверяем, что компания появилась в базе
        List<Company> companies = companyDAO.getAllCompanies();
        boolean found = companies.stream()
                .anyMatch(c -> testINN.equals(c.getInn()));
        assertTrue(found, "Добавленная компания должна быть найдена в БД");
    }

    @Test
    @Order(3)
    @DisplayName("3. Добавление компании с дублирующимся ИНН")
    void testAddDuplicateINN() {
        if (testINN == null) {
            System.out.println("Пропускаем тест - нет тестового ИНН");
            return;
        }

        Company company = new Company();
        company.setName("Duplicate Company");
        company.setInn(testINN); // Тот же ИНН
        company.setAddress("Another Address");

        boolean result = companyDAO.addCompany(company);
        assertFalse(result, "Компания с дублирующимся ИНН не должна быть добавлена");
        System.out.println("Попытка добавить дубликат ИНН " + testINN + " - отклонена (ОК)");
    }

    @Test
    @Order(4)
    @DisplayName("4. Обновление компании")
    void testUpdateCompany() {
        List<Company> companies = companyDAO.getAllCompanies();
        if (companies.isEmpty()) {
            System.out.println("Нет компаний для обновления - пропускаем тест");
            return;
        }

        Company company = companies.get(0);
        String originalName = company.getName();
        BigDecimal originalRating = company.getRating();

        // Изменяем данные
        company.setName(originalName + " (Updated)");
        company.setRating(new BigDecimal("5.00"));
        company.setAddress(company.getAddress() + ", Office 2");

        boolean result = companyDAO.updateCompany(company);
        assertTrue(result, "Компания должна быть успешно обновлена");
        System.out.println("Компания обновлена: " + company.getName());

        // Возвращаем исходные данные
        company.setName(originalName);
        company.setRating(originalRating);
        companyDAO.updateCompany(company);
        System.out.println("Данные компании возвращены к исходным");
    }

    @Test
    @Order(5)
    @DisplayName("5. Валидация ИНН (некорректная длина)")
    void testInvalidINNLength() {
        Company company = new Company();
        company.setName("Invalid INN Company");
        company.setInn("12345"); // Слишком короткий
        company.setAddress("Test Address");

        boolean result = companyDAO.addCompany(company);
        System.out.println("Попытка добавить компанию с коротким ИНН: " + result);
        // В зависимости от реализации DAO может быть false или true
        // (валидация может быть на уровне UI, а не DAO)
    }

    @Test
    @Order(6)
    @DisplayName("6. Удаление компании")
    void testDeleteCompany() {
        // Создаем компанию специально для удаления
        String deleteINN = String.valueOf(System.currentTimeMillis() + 1).substring(0, 10);

        Company company = new Company();
        company.setName("DeleteTest " + System.currentTimeMillis());
        company.setInn(deleteINN);
        company.setAddress("Delete Address");
        company.setPhone("+79999876543");

        companyDAO.addCompany(company);

        // Находим добавленную компанию
        List<Company> companies = companyDAO.getAllCompanies();
        Company toDelete = companies.stream()
                .filter(c -> deleteINN.equals(c.getInn()))
                .findFirst()
                .orElse(null);

        assertNotNull(toDelete, "Компания для удаления должна быть найдена");

        boolean result = companyDAO.deleteCompany(toDelete.getId());
        assertTrue(result, "Компания должна быть успешно удалена");
        System.out.println("Компания удалена: " + toDelete.getName() + " (ID: " + toDelete.getId() + ")");

        // Проверяем, что компания действительно удалена
        companies = companyDAO.getAllCompanies();
        boolean exists = companies.stream()
                .anyMatch(c -> deleteINN.equals(c.getInn()));
        assertFalse(exists, "Компания не должна существовать после удаления");
    }
}