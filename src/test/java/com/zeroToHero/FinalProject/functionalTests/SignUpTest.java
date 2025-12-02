package com.zeroToHero.FinalProject.functionalTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignUpTest {
    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        // Sửa đường dẫn driver của bạn ở đây
        System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void testSignUpSuccess() {
        driver.get(BASE_URL + "/sign-up");

        // Tạo email ngẫu nhiên để không bị trùng lặp khi chạy test nhiều lần
        String randomEmail = "user" + System.currentTimeMillis() + "@test.com";

        // Điền thông tin (Dựa trên id/name trong file sign_up.jsp)
        driver.findElement(By.name("firstName")).sendKeys("Nguyen");
        driver.findElement(By.name("lastName")).sendKeys("Van Test");
        driver.findElement(By.name("email")).sendKeys(randomEmail);
        driver.findElement(By.name("password")).sendKeys("123456");

        // Click nút Đăng ký (là thẻ <input type="submit">)
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Kiểm tra: Đăng ký thành công phải chuyển về trang Login (theo SignUpServlet.java)
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/log-in"), "Đăng ký xong không chuyển về trang đăng nhập!");
    }
}