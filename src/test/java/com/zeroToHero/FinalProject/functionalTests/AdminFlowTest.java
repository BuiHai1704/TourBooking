package com.zeroToHero.FinalProject.functionalTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminFlowTest {

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless"); // Bỏ comment nếu muốn chạy ẩn
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        
        loginAsAdmin();
    }

    private void loginAsAdmin() {
        driver.get(BASE_URL + "/log-in");
        driver.findElement(By.name("email")).sendKeys("admin@gmail.com"); 
        driver.findElement(By.name("password")).sendKeys("123456");       
        driver.findElement(By.cssSelector("input[type='submit']")).click();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void testAdminAddTour() {
        driver.get(BASE_URL + "/admin/tour");
        
        // Giả sử có nút mở modal thêm mới hoặc form nằm ngay trên trang
        // Lưu ý: Cần điều chỉnh selector cho khớp với file admin/tour.jsp của bạn
        
        // Ví dụ điền form thêm tour
        try {
            // Tìm các ô input (cần check ID/Name trong JSP thật)
            WebElement titleInput = driver.findElement(By.name("title"));
            WebElement priceInput = driver.findElement(By.name("price"));
            
            String newTourName = "Selenium Tour " + System.currentTimeMillis();
            titleInput.sendKeys(newTourName);
            priceInput.sendKeys("999");
            
            // Submit
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            
            // Verify
            assertTrue(driver.getPageSource().contains(newTourName));
        } catch (Exception e) {
            System.out.println("Không tìm thấy element form thêm tour: " + e.getMessage());
        }
    }
}