import static com.codeborne.selenide.Selenide.*;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class FatsecretSelenide {

    @BeforeEach
    void setUp() {
        Configuration.timeout = 10000;
        Configuration.pageLoadStrategy = "eager";
    }

    /*
     * Тест: Проверка ошибки при пустых полях
     *
     * На странице https://foods.fatsecret.com/
     * 1. Нажать Sign In
     * 2. Очистить поля
     * 3. Нажать Sign In
     * 4. Проверить, что получено сообщение об ошибке (alert)
     */
    @Test
    public void test01EmptyLoginError() {
        open("https://foods.fatsecret.com/");

        $x("//a[text()='Sign In']").click();

        $(By.id("ctl11_Logincontrol1_Name")).clear();
        $(By.id("ctl11_Logincontrol1_Password")).clear();

        $(By.id("ctl11_Logincontrol1_Login")).click();

        //Закрываем alert с сообщением об ошибке
        confirm();

        System.out.println("Тест пройден: сообщение об ошибке получено!");
    }
}

