package com.zeroToHero.FinalProject.models.dao;

import com.zeroToHero.FinalProject.database.DBConnectionManager;
import com.zeroToHero.FinalProject.models.beans.Users;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsersDAOIntegrationTest {

    // Cấu hình kết nối H2 Database giả lập PostgreSQL
    private static final String JDBC_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private MockedStatic<DBConnectionManager> mockedDBUtils;
    private Connection h2Connection;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Tạo kết nối thật tới H2 Database
        h2Connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        // 2. Tạo bảng users trong H2
        RunScript.execute(h2Connection, new FileReader("src/test/resources/schema.sql"));

        // 3. Bẻ lái: Mock static method để trả về kết nối H2 thay vì JNDI
        mockedDBUtils = Mockito.mockStatic(DBConnectionManager.class);
        mockedDBUtils.when(DBConnectionManager::getConnection).thenReturn(h2Connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Đóng mock và kết nối sau mỗi bài test
        if (mockedDBUtils != null) {
            mockedDBUtils.close();
        }
        if (h2Connection != null) {
            h2Connection.close();
        }
    }

    @Test
    void testSignUpAndCheckEmail() throws SQLException {
        UsersDAO dao = new UsersDAO();

        // --- TEST CASE 1: Đăng ký user mới (Insert) ---
        Users newUser = new Users();
        String userId = UUID.randomUUID().toString();
        newUser.setUserId(userId);
        newUser.setEmail("integration@test.com");
        newUser.setPassword("hashedPassword123");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setRole("USER");
        newUser.setCountryId(1L);

        // Gọi hàm signUp (Code này sẽ chạy SQL Insert thật vào H2)
        dao.signUp(newUser);

        // --- TEST CASE 2: Kiểm tra email đã tồn tại (Select) ---
        // Nếu insert thành công, hàm này phải trả về true
        boolean exists = dao.checkAvailableEmail("integration@test.com");
        assertTrue(exists, "Email vừa đăng ký phải tồn tại trong DB");

        // Kiểm tra email lạ -> phải trả về false
        boolean notExists = dao.checkAvailableEmail("fake@test.com");
        assertFalse(notExists, "Email chưa đăng ký không được tồn tại");
    }
}