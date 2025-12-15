import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SearchTest {

    //Для навигации в браузере
    private WebDriver driver;
    private WebDriverWait wait;

    //Настройка для каждого теста
    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Меняем стратегию загрузки страницы
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        // Инициализируем драйвер с этими настройками
        driver = new ChromeDriver(options);
        // Инициализируем объект ожидания (максимум 10 секунд)
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void loginTest() throws InterruptedException {
        //Открыть страницу входа
        driver.get("https://foods.fatsecret.com/Auth.aspx?pa=s&ReturnUrl=https%3a%2f%2ffoods.fatsecret.com%2fDefault.aspx%3fpa%3dm");
        Thread.sleep(2000); // Даем странице загрузиться

        // Найти поле для имени пользователя/email
        WebElement usernameField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("ctl11_Logincontrol1_Name"))
        );

        //Найти поле для пароля
        WebElement passwordField = driver.findElement(
                By.id("ctl11_Logincontrol1_Password")
        );

        //Найти кнопку входа
        WebElement loginButton = driver.findElement(
                By.id("ctl11_Logincontrol1_Login")
        );

        //Ввести учетные данные
        usernameField.clear();
        usernameField.sendKeys("sbayg@comfythings.com");

        passwordField.clear();
        passwordField.sendKeys("javatest1234");

        //Нажать кнопку входа
        loginButton.click();

        //Подождать и проверить успешный вход
        Thread.sleep(5000); // Ждем редирект после входа

        //Проверяем URL
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Текущий URL: " + currentUrl);

        if (currentUrl.contains("Default.aspx?pa=m")) {
            System.out.println("Вход выполнен успешно!");
        } else {
            System.out.println("Что-то пошло не так. Ожидался URL с Default.aspx?pa=m");
        }
    }

    //Тестирование поиска продуктов (Все 10 продуктов на странице содержат искомую подстроку)
    @Test
    public void searchTest() throws InterruptedException {
        //Открыть страницу
        driver.get("https://foods.fatsecret.com/calories-nutrition/");

        //задержка до загрузки поля ввода
        Thread.sleep(3000);

        //Поле ввода текста поиска
        WebElement searchBox = driver.findElement(By.id("ctl11_ctl05_ByFood"));
        //Кнопка поиска
        WebElement buttonSearch = driver.findElement(By.cssSelector("img[title='search for nutritional information']"));

        //Ввод текста поиска
        searchBox.click();
        // Выделяем всё (Ctrl+A) и удаляем (Delete) (А то Js скрипт мешает)
        searchBox.sendKeys(Keys.CONTROL + "a");
        searchBox.sendKeys(Keys.DELETE);
        searchBox.sendKeys("tomato");
        //Нажать на кнопку поиска
        buttonSearch.click();

        //Задержка до загрузки страницы результатов
        Thread.sleep(1000);

        // Находим все элементы продуктов в таблице
        List<WebElement> productLinks = driver.findElements(
                By.cssSelector("table.generic.searchResult a.prominent"));



        // Проверяем каждый результат
        int tomatoCount = 0;
        for (int i = 0; i < productLinks.size(); i++) {
            WebElement productLink = productLinks.get(i);
            String productName = productLink.getText().toLowerCase();

            // Проверяем содержит ли "tomato"
            Assertions.assertTrue(productName.contains("tomato"),
                    "Результат #" + (i + 1) + " '" + productLink.getText() +
                            "' не содержит 'tomato'");

            if (productName.contains("tomato")) {
                tomatoCount++;
            }

            System.out.println((i + 1) + ". " + productLink.getText() +
                    " - содержит 'tomato': " + productName.contains("tomato"));
        }

    }

    //Тестирование страницы подсчёта калорий
    @Test
    public void caloriesTest() throws InterruptedException {
        //Повторить логин
        loginTest();
        driver.get("https://foods.fatsecret.com/Diary.aspx?pa=fj&dt=20437");

        // Ждем и кликаем кнопку "Добавить завтрак"
        WebElement addBreakfastItem = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("addBfast"))
        );
        addBreakfastItem.click();

        //Ждем и находим поле поиска
        WebElement searchFood = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchBfastExp"))
        );
        searchFood.click();
        searchFood.sendKeys("strawberries");

        //Ждем и кликаем кнопку поиска (используем более гибкий селектор)
        WebElement searchButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("img[src*='But_icon_Search']"))
        );
        searchButton.click();

        //Ждем появления первого чекбокса и кликаем
        Thread.sleep(1000); // Короткая пауза для стабилизации (можно заменить на ожидание)
        WebElement firstCheckbox = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("img[src*='FA_item_Unselected']"))
        );
        firstCheckbox.click();

        //Ждем и кликаем кнопку "Add Selected"
        WebElement addStrawberry = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[contains(., 'Add Selected')]")));

        //Ждем появления элемента с калориями и проверяем текст
        Thread.sleep(3000); // Даем время для применения изменений

        // Используем contains для более надежного поиска по частичному тексту[citation:6][citation:9]
        WebElement caloriesElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(text(), 'calories')]"))
        );

        //Проверяем, что добавилось 32 калория
        String caloriesText = caloriesElement.getText().trim();
        Assertions.assertEquals("(32 calories)", caloriesText);
        System.out.println("Успех! Найден текст: " + caloriesText);
    }

    //Добавление записи в журнал
    @Test
    public void recordJournalTest() throws InterruptedException {
        //Повторить логин
        loginTest();

        //Проверяемый текст
        String testString = "New record from test";

        //Переход на страницу добавления записи в журнал
        driver.get("https://foods.fatsecret.com/Default.aspx?pa=memn&id=139209114&new=y");
        //Поиск поля ввода текста
        WebElement inputText = driver.findElement(By.id("ctl11_JournalEntryCapture_TextInput"));
        inputText.click();
        //Записываем в него тестовую строку
        inputText.sendKeys(testString);
        Thread.sleep(1000);
        //Ищем кнопку сохранения по ссылке JS
        WebElement saveButton = driver.findElement(
                By.cssSelector("a[href*='__doPostBack']"));
        //Нажимаем на сохранение
        saveButton.click();
        Thread.sleep(3000);
        //Переходим на страницу записей журнала
        driver.get("https://foods.fatsecret.com/Default.aspx?pa=memn&id=139209114");
        //Ищем первую (по времени - последнюю) запись журнала
        WebElement firstRecord = driver.findElement(
                By.cssSelector("a.plainText"));

        // Получить текст
        String firstRecordText = firstRecord.getText();
        //Сравниваем текст
        Assertions.assertEquals(testString, firstRecordText);

    }

    //По окончании всех тестов - закрыть окно браузера
    @AfterEach
    public void afterTests() {
        if (driver != null) {
            //Выход из браузера
            driver.quit();
        }
    }

}

