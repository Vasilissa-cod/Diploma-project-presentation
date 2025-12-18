import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FatsecretTestSelenide {

    // Учетные данные для входа
    private static final String USERNAME = "sbayg@comfythings.com";
    private static final String PASSWORD = "javatest1234";

    @BeforeEach
    void setUp() {
        open("https://foods.fatsecret.com/");
        getWebDriver().manage().window().maximize();
    }

    // 1. Проверка ошибки при пустых полях
    @Test
    void test01EmptyLoginError() {
        $x("//a[text()='Sign In']").click();
        $(By.id("ctl11_Logincontrol1_Name")).shouldBe(visible).clear();
        $(By.id("ctl11_Logincontrol1_Password")).shouldBe(visible).clear();
        $(By.id("ctl11_Logincontrol1_Login")).click();
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains("error") || alert.getText().contains("required") || alert.getText().length() > 0);
        alert.accept();
    }

    // 2. Авторизация на сайте
    @Test
    void test02Login() {
        open("https://foods.fatsecret.com/Auth.aspx?pa=s&ReturnUrl=https%3a%2f%2ffoods.fatsecret.com%2fDefault.aspx%3fpa%3dm");
        $(By.id("ctl11_Logincontrol1_Name")).shouldBe(visible).setValue(USERNAME);
        $(By.id("ctl11_Logincontrol1_Password")).shouldBe(visible).setValue(PASSWORD);
        $(By.id("ctl11_Logincontrol1_Login")).click();
        sleep(3000);
        String currentUrl = webdriver().driver().url();
        assertTrue(currentUrl.contains("Default.aspx?pa=m"));
    }

    // 3. Поиск продуктов
    @Test
    void test03Search() {
        open("https://foods.fatsecret.com/calories-nutrition/");
        $(By.id("ctl11_ctl05_ByFood")).click();
        $(By.id("ctl11_ctl05_ByFood")).sendKeys(Keys.CONTROL + "a");
        $(By.id("ctl11_ctl05_ByFood")).sendKeys(Keys.DELETE);
        $(By.id("ctl11_ctl05_ByFood")).setValue("tomato");
        $("img[title='search for nutritional information']").click();
        sleep(1000);
        $("table.generic.searchResult a.prominent").shouldHave(text("tomato"));
    }

    // 4. Подсчет калорий
    @Test
    void test04Calories() {
        performLoginIfNeeded();
        open("https://foods.fatsecret.com/Diary.aspx?pa=fj&dt=20437");
        $(By.id("addBfast")).click();
        $(By.id("searchBfastExp")).setValue("strawberries");
        $("img[src*='But_icon_Search']").click();
        sleep(1000);
        $("img[src*='FA_item_Unselected']").click();
        $x("//span[contains(., 'Add Selected')]").click();
        sleep(3000);
        $x("//div[contains(text(), 'calories')]").shouldHave(text("calories"));
    }

    // 5. Добавление записи в журнал
    @Test
    void test05RecordJournal() {
        performLoginIfNeeded();
        String testString = "New record from test";
        open("https://foods.fatsecret.com/Default.aspx?pa=memn&id=139209114&new=y");
        $(By.id("ctl11_JournalEntryCapture_TextInput")).setValue(testString);
        sleep(1000);
        $("a[href*='__doPostBack']").click();
        sleep(3000);
        open("https://foods.fatsecret.com/Default.aspx?pa=memn&id=139209114");
        $("a.plainText").shouldHave(text(testString));
    }

    // Вспомогательный метод для авторизации (если не залогинен)
    private void performLoginIfNeeded() {
        open("https://foods.fatsecret.com/Auth.aspx?pa=s&ReturnUrl=https%3a%2f%2ffoods.fatsecret.com%2fDefault.aspx%3fpa%3dm");
        sleep(2000);
        String currentUrl = webdriver().driver().url();
        // Если уже залогинен (редирект на главную), пропускаем авторизацию
        if (currentUrl.contains("Default.aspx") && !currentUrl.contains("Auth.aspx")) {
            return;
        }
        // Если форма авторизации присутствует, выполняем вход
        try {
            $x("//input[@id='ctl11_Logincontrol1_Name']").shouldBe(visible).setValue(USERNAME);
            $x("//input[@id='ctl11_Logincontrol1_Password']").shouldBe(visible).setValue(PASSWORD);
            $x("//input[@id='ctl11_Logincontrol1_Login']").click();
            sleep(3000);
        } catch (Exception e) {
            // Если элементы не найдены, возможно уже залогинен
        }
    }
}
