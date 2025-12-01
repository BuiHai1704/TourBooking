package functionalTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080"; // Đổi port nếu bạn chạy port khác

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver-win64\\chromedriver.exe");
        // Khởi tạo trình duyệt Chrome
        driver = new ChromeDriver();
        
        // Thiết lập thời gian chờ ngầm định (Implicit wait) để tìm phần tử
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        // Đóng trình duyệt sau khi test xong
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginSuccess() {
        // 1. Truy cập trang đăng nhập
        driver.get(BASE_URL + "/log-in");

        // 2. Tìm và điền thông tin đăng nhập đúng (Dữ liệu này phải có sẵn trong DB)
        WebElement emailField = driver.findElement(By.name("email"));
        WebElement passwordField = driver.findElement(By.name("password"));
        
        emailField.sendKeys("admin@gmail.com"); // Thay bằng email admin thật trong DB của bạn
        passwordField.sendKeys("123456");       // Thay bằng mật khẩu thật

        // 3. Tìm nút submit và click (Giả sử nút là input type='submit' hoặc button)
        WebElement loginBtn = driver.findElement(By.cssSelector("input[type='submit']"));
        loginBtn.click(); // <-- THỰC HIỆN CLICK ĐĂNG NHẬP

        // 4. Chờ và kiểm tra kết quả mong đợi (Chuyển về trang chủ)
        // Sử dụng WebDriverWait để chờ trang chuyển hướng, giúp test ổn định hơn
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));

        String expectedUrl = BASE_URL + "/";
        String actualUrl = driver.getCurrentUrl();
        
        // Assert: Nếu URL thực tế khớp với URL mong đợi -> Test Pass
        assertEquals(expectedUrl, actualUrl, "Đăng nhập thành công phải chuyển về trang chủ!");
    }

    @Test
    public void testLoginFailure() {
        // 1. Truy cập trang đăng nhập
        driver.get(BASE_URL + "/log-in");

        // 2. Điền thông tin sai
        driver.findElement(By.name("email")).sendKeys("wronguser@test.com");
        driver.findElement(By.name("password")).sendKeys("wrongpassword");

        // 3. Click nút đăng nhập
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // 4. Kiểm tra kết quả
        // Cách tốt nhất là chờ và kiểm tra thông báo lỗi xuất hiện.
        // Selector này có thể cần được điều chỉnh cho phù hợp với giao diện của bạn.
        // Ví dụ: <div class="alert alert-danger">...</div>
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger")));

        // Kiểm tra xem thông báo lỗi có hiển thị không
        assertTrue(errorMsg.isDisplayed(), "Thông báo lỗi đăng nhập thất bại không hiển thị!");
        assertNotNull(errorMsg.getText(), "Thông báo lỗi không được để trống!");

        // Đồng thời, có thể kiểm tra URL vẫn là trang đăng nhập
        assertTrue(driver.getCurrentUrl().contains("/log-in"), "Đăng nhập sai nhưng URL đã bị thay đổi!");
    }

}