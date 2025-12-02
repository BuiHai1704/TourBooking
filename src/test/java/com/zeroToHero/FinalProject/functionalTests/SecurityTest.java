package com.zeroToHero.FinalProject.functionalTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityTest {
    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
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
    public void testAdminAccessDeniedForNormalUser() {
        // 1. Đăng nhập bằng tài khoản USER (Không phải Admin)
        driver.get(BASE_URL + "/log-in");
        driver.findElement(By.name("email")).sendKeys("user@gmail.com"); // Tài khoản user thường lấy từ PopulateQuery
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // 2. Cố tình truy cập vào trang Admin
        String adminUrl = BASE_URL + "/admin/dashboard"; // Hoặc URL admin bất kỳ của dự án
        driver.get(adminUrl);

        // 3. Kiểm tra: Phải bị chuyển hướng về Trang chủ (BASE_URL + "/")
        // Logic này nằm trong file AdminFilter.java: response.sendRedirect(request.getContextPath() + "/");
        String currentUrl = driver.getCurrentUrl();
        
        // Lưu ý: Browser có thể thêm dấu / ở cuối hoặc không, nên dùng startsWith hoặc so sánh linh hoạt
        assertEquals(BASE_URL + "/", currentUrl, "User thường vào trang Admin mà không bị chặn!");
    }
}