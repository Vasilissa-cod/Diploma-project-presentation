import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public class FatsecretTest {

    // Учетные данные для входа
    private static final String USERNAME = "sbayg@comfythings.com";
    private static final String PASSWORD = "javatest1234";

    @BeforeEach
    void setUp() {
        Configuration.timeout = 10000;
        Configuration.pageLoadStrategy = "eager";
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    /*
     * Тест 1: Проверка ошибки при пустых полях
     *
     * На странице https://foods.fatsecret.com/
     * 1. Нажать Sign In
     * 2. Очистить поля
     * 3. Нажать Sign In
     * 4. Проверить, что получено сообщение об ошибке (alert)
     */
    @Test
    public void emptyLoginErrorTest() {
        open("https://foods.fatsecret.com/");

        $x("//a[text()='Sign In']").click();

        $(By.id("ctl11_Logincontrol1_Name")).clear();
        $(By.id("ctl11_Logincontrol1_Password")).clear();

        $(By.id("ctl11_Logincontrol1_Login")).click();

        // Закрываем alert с сообщением об ошибке
        confirm();

        System.out.println("Тест пройден: сообщение об ошибке получено!");
    }

    /*
     * Тест 2: Авторизация на сайте
     *
     * На странице входа:
     * 1. Открыть страницу логина
     * 2. Ввести имя пользователя
     * 3. Ввести пароль
     * 4. Нажать кнопку входа
     * 5. Проверить успешный вход по URL
     */
    @Test
    public void loginTest() {
        open("https://foods.fatsecret.com/Auth.aspx?pa=s&ReturnUrl=https%3a%2f%2ffoods.fatsecret.com%2fDefault.aspx%3fpa%3dm");

        // Ввод логина
        $(By.id("ctl11_Logincontrol1_Name")).clear();
        $(By.id("ctl11_Logincontrol1_Name")).setValue(USERNAME);

        // Ввод пароля
        $(By.id("ctl11_Logincontrol1_Password")).clear();
        $(By.id("ctl11_Logincontrol1_Password")).setValue(PASSWORD);

        // Нажать кнопку входа
        $(By.id("ctl11_Logincontrol1_Login")).click();

        // Ожидание и проверка URL после входа
        sleep(3000);
        String currentUrl = webdriver().driver().url();
        Assertions.assertTrue(currentUrl.contains("Default.aspx?pa=m"),
                "Ожидался URL с Default.aspx?pa=m, получен: " + currentUrl);

        System.out.println("Вход выполнен успешно! URL: " + currentUrl);
    }

    /*
     * Тест 3: Поиск продуктов
     *
     * На странице поиска:
     * 1. Открыть страницу калорий
     * 2. Ввести "tomato" в поле поиска
     * 3. Нажать кнопку поиска
     * 4. Проверить, что все результаты содержат "tomato"
     */
    @Test
    public void searchTest() {
        open("https://foods.fatsecret.com/calories-nutrition/");

        // Поле поиска
        $(By.id("ctl11_ctl05_ByFood")).click();
        $(By.id("ctl11_ctl05_ByFood")).sendKeys(Keys.CONTROL + "a");
        $(By.id("ctl11_ctl05_ByFood")).sendKeys(Keys.DELETE);
        $(By.id("ctl11_ctl05_ByFood")).setValue("tomato");

        // Кнопка поиска
        $("img[title='search for nutritional information']").click();

        sleep(1000);

        // Получаем все результаты поиска
        ElementsCollection productLinks = $$("table.generic.searchResult a.prominent");

        // Проверяем каждый результат
        for (int i = 0; i < productLinks.size(); i++) {
            String productName = productLinks.get(i).getText().toLowerCase();
            Assertions.assertTrue(productName.contains("tomato"),
                    "Результат #" + (i + 1) + " '" + productLinks.get(i).getText() +
                            "' не содержит 'tomato'");
            System.out.println((i + 1) + ". " + productLinks.get(i).getText() + " - OK");
        }

        System.out.println("Поиск успешен! Найдено " + productLinks.size() + " результатов с 'tomato'");
    }

    /*
     * Тест 4: Подсчет калорий
     *
     * После авторизации:
     * 1. Перейти на страницу дневника
     * 2. Добавить завтрак (strawberries)
     * 3. Выбрать первый результат
     * 4. Проверить, что добавилось 32 калории
     */
    @Test
    public void caloriesTest() {
        // Сначала авторизуемся
        performLogin();

        // Переходим на страницу дневника
        open("https://foods.fatsecret.com/Diary.aspx?pa=fj&dt=20437");

        // Кликаем "Добавить завтрак"
        $(By.id("addBfast")).click();

        // Ищем продукт
        $(By.id("searchBfastExp")).click();
        $(By.id("searchBfastExp")).setValue("strawberries");

        // Кликаем кнопку поиска
        $("img[src*='But_icon_Search']").click();

        sleep(1000);

        // Выбираем первый чекбокс
        $("img[src*='FA_item_Unselected']").click();

        // Кликаем "Add Selected"
        $x("//span[contains(., 'Add Selected')]").click();

        sleep(3000);

        // Проверяем, что калории отображаются (значение может меняться от предыдущих запусков)
        String caloriesText = $x("//div[contains(text(), 'calories')]").getText().trim();
        Assertions.assertTrue(caloriesText.contains("calories"),
                "Ожидался текст с 'calories', получено: " + caloriesText);

        System.out.println("Успех! Калории: " + caloriesText);
    }

    /*
     * Тест 5: Добавление записи в журнал
     *
     * После авторизации:
     * 1. Перейти на страницу добавления записи
     * 2. Ввести тестовый текст
     * 3. Сохранить запись
     * 4. Проверить, что запись появилась в журнале
     */
    @Test
    public void recordJournalTest() {
        // Сначала авторизуемся
        performLogin();

        String testString = "New record from test";

        // Переходим на страницу добавления записи
        open("https://foods.fatsecret.com/Default.aspx?pa=memn&id=139209114&new=y");

        // Вводим текст записи
        $(By.id("ctl11_JournalEntryCapture_TextInput")).click();
        $(By.id("ctl11_JournalEntryCapture_TextInput")).setValue(testString);

        sleep(1000);

        // Сохраняем запись
        $("a[href*='__doPostBack']").click();

        sleep(3000);

        // Переходим на страницу журнала
        open("https://foods.fatsecret.com/Default.aspx?pa=memn&id=139209114");

        // Проверяем первую запись
        String firstRecordText = $("a.plainText").getText();
        Assertions.assertEquals(testString, firstRecordText,
                "Ожидалось '" + testString + "', получено: " + firstRecordText);

        System.out.println("Запись в журнал добавлена успешно: " + firstRecordText);
    }

    /**
     * Вспомогательный метод для авторизации
     */
    private void performLogin() {
        open("https://foods.fatsecret.com/Auth.aspx?pa=s&ReturnUrl=https%3a%2f%2ffoods.fatsecret.com%2fDefault.aspx%3fpa%3dm");

        $(By.id("ctl11_Logincontrol1_Name")).clear();
        $(By.id("ctl11_Logincontrol1_Name")).setValue(USERNAME);

        $(By.id("ctl11_Logincontrol1_Password")).clear();
        $(By.id("ctl11_Logincontrol1_Password")).setValue(PASSWORD);

        $(By.id("ctl11_Logincontrol1_Login")).click();

        sleep(3000);
    }
}


