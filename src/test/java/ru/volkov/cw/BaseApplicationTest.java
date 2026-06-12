package ru.volkov.cw;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TableViewMatchers;
import ru.volkov.cw.dao.*;
import ru.volkov.cw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

public abstract class BaseApplicationTest extends ApplicationTest {

    protected static final String TEST_USERNAME = "admin";
    protected static final String TEST_PASSWORD = "admin123";

    // DAO для очистки тестовых данных
    protected final ProductDAO productDAO = new ProductDAO();
    protected final StoreDAO storeDAO = new StoreDAO();
    protected final CompanyDAO companyDAO = new CompanyDAO();
    protected final CategoryDAO categoryDAO = new CategoryDAO();
    protected final BrandDAO brandDAO = new BrandDAO();
    protected final UnitOfMeasureDAO unitDAO = new UnitOfMeasureDAO();
    protected final DeliveryDAO deliveryDAO = new DeliveryDAO();

    // Списки для очистки после тестов
    protected final List<Product> productsToClean = new ArrayList<>();
    protected final List<Store> storesToClean = new ArrayList<>();
    protected final List<Company> companiesToClean = new ArrayList<>();
    protected final List<Category> categoriesToClean = new ArrayList<>();
    protected final List<Brand> brandsToClean = new ArrayList<>();
    protected final List<UnitOfMeasure> unitsToClean = new ArrayList<>();
    protected final List<Delivery> deliveriesToClean = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        new MainApplication().start(stage);
    }

    @BeforeEach
    void setUp() {
        // Ждем загрузки окна логина
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void cleanUp() {
        // Очистка товаров
        for (Product p : productsToClean) {
            try {
                productDAO.deleteProduct(p.getId());
            } catch (Exception ignored) {}
        }
        productsToClean.clear();

        // Очистка магазинов
        for (Store s : storesToClean) {
            try {
                storeDAO.deleteStore(s.getId());
            } catch (Exception ignored) {}
        }
        storesToClean.clear();

        // Очистка компаний
        for (Company c : companiesToClean) {
            try {
                companyDAO.deleteCompany(c.getId());
            } catch (Exception ignored) {}
        }
        companiesToClean.clear();

        // Очистка категорий
        for (Category c : categoriesToClean) {
            try {
                categoryDAO.deleteCategory(c.getId());
            } catch (Exception ignored) {}
        }
        categoriesToClean.clear();

        // Очистка брендов
        for (Brand b : brandsToClean) {
            try {
                brandDAO.deleteBrand(b.getId());
            } catch (Exception ignored) {}
        }
        brandsToClean.clear();

        // Очистка единиц измерения
        for (UnitOfMeasure u : unitsToClean) {
            try {
                unitDAO.deleteUnit(u.getId());
            } catch (Exception ignored) {}
        }
        unitsToClean.clear();

        // Очистка поставок
        for (Delivery d : deliveriesToClean) {
            try {
                deliveryDAO.deleteDelivery(d.getId());
            } catch (Exception ignored) {}
        }
        deliveriesToClean.clear();
    }

    /**
     * Выполняет вход в систему
     */
    protected void performLogin() {
        clickOn("#txtUsername").write(TEST_USERNAME);
        clickOn("#txtPassword").write(TEST_PASSWORD);
        clickOn("#btnLogin");

        // Ждем загрузки главного окна
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Переключается на указанную вкладку по индексу
     */
    protected void switchToTab(int tabIndex) {
        TabPane tabPane = lookup("#mainTabPane").query();
        interact(() -> tabPane.getSelectionModel().select(tabIndex));
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Очищает текстовое поле
     */
    protected void clearTextField(String textFieldId) {
        clickOn("#" + textFieldId);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.BACK_SPACE);
    }

    /**
     * Вводит текст в поле, предварительно очистив его
     */
    protected void writeText(String textFieldId, String text) {
        clearTextField(textFieldId);
        clickOn("#" + textFieldId).write(text);
    }
}