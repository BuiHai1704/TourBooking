package com.zeroToHero.FinalProject.models.dao;

import com.zeroToHero.FinalProject.database.DBConnectionManager;
import com.zeroToHero.FinalProject.models.beans.Tours;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ToursDAOIntegrationTest {

    private static final String JDBC_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private MockedStatic<DBConnectionManager> mockedDBUtils;
    private Connection h2Connection;

    @BeforeEach
    void setUp() throws Exception {
        h2Connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        RunScript.execute(h2Connection, new FileReader("src/test/resources/schema.sql"));

        mockedDBUtils = Mockito.mockStatic(DBConnectionManager.class);
        mockedDBUtils.when(DBConnectionManager::getConnection).thenReturn(h2Connection);
        
        // Chuẩn bị dữ liệu mẫu (Tour + Country + City + Destination)
        seedData();
    }

    private void seedData() throws SQLException {
        // 1. Tạo Country
        h2Connection.createStatement().execute("INSERT INTO countries (name) VALUES ('Vietnam')");
        // 2. Tạo City (country_id = 1)
        h2Connection.createStatement().execute("INSERT INTO cities (name, country_id) VALUES ('Hanoi', 1)");
        // 3. Tạo Tour (Lưu ý: H2 dùng ARRAY[...] cho cột images)
        h2Connection.createStatement().execute("INSERT INTO tours (title, duration, price, images) VALUES ('Ha Long Bay', 3, '500$', ARRAY['img1.jpg'])");
        // 4. Link Tour với City qua Destinations (city_id=1, tour_id=1)
        h2Connection.createStatement().execute("INSERT INTO destinations (city_id, tour_id) VALUES (1, 1)");
    }

    @AfterEach
    void tearDown() throws SQLException {
        mockedDBUtils.close();
        h2Connection.close();
    }

    @Test
    void testGetPopularTours() {
        ToursDAO dao = new ToursDAO();
        ArrayList<Tours> popularTours = dao.getPopularTours();

        assertNotNull(popularTours);
        assertFalse(popularTours.isEmpty(), "Phải lấy được ít nhất 1 tour");
        
        Tours t = popularTours.get(0);
        assertEquals("Ha Long Bay", t.getTitle());
        assertEquals("Vietnam", t.getCountryName(), "Phải Join bảng countries để lấy tên nước");
        assertEquals("img1.jpg", t.getImages(), "Phải parse được mảng hình ảnh từ DB");
    }
    
    @Test
    void testCount() {
        ToursDAO dao = new ToursDAO();
        int count = dao.count();
        assertEquals(1, count, "Tổng số tour trong DB phải là 1");
    }
}