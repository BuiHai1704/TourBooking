package com.zeroToHero.FinalProject.functionalTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingTest {
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
    public void testBookingProcess() {
        // --- BƯỚC 1: ĐĂNG NHẬP (Bắt buộc) ---
        driver.get(BASE_URL + "/log-in");
        driver.findElement(By.name("email")).sendKeys("user@gmail.com"); // Tài khoản user thường
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // --- BƯỚC 2: VÀO CHI TIẾT TOUR ---
        // Giả sử vào tour có ID=1 (Hà Nội)
        driver.get(BASE_URL + "/tour/TourServlet?TourId=1");

        // --- BƯỚC 3: ĐIỀN NGÀY ĐI ---
        // Trong tour.jsp dùng <input type="date">
        WebElement checkInInput = driver.findElement(By.id("check-in-date"));
        WebElement checkOutInput = driver.findElement(By.id("check-out-date"));

        // Gửi phím ngày tháng (định dạng mmddyyyy hoặc ddmmyyyy tùy locale máy tính)
        // Cách an toàn nhất là dùng JavaScript để set value trực tiếp
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = '2025-12-20';", checkInInput);
        js.executeScript("arguments[0].value = '2025-12-25';", checkOutInput);

        // --- BƯỚC 4: CLICK ĐẶT TOUR ---
        // Trong tour.jsp nút đặt tour là <button type="submit">
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // --- BƯỚC 5: KIỂM TRA KẾT QUẢ ---
        // Theo BookingsServlet.java, thành công sẽ chuyển hướng về "/me" (Trang cá nhân)
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/me"), "Đặt tour xong không chuyển về trang cá nhân (/me)!");
    }
}