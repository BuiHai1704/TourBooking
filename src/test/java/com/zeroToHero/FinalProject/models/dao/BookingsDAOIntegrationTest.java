package com.zeroToHero.FinalProject.models.dao;

import com.zeroToHero.FinalProject.database.DBConnectionManager;
import com.zeroToHero.FinalProject.models.beans.Bookings;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingsDAOIntegrationTest {

    private static final String JDBC_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private MockedStatic<DBConnectionManager> mockedDBUtils;
    private Connection h2Connection;

    @BeforeEach
    void setUp() throws Exception {
        h2Connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        // Chạy lại schema.sql để tạo bảng cần thiết
        RunScript.execute(h2Connection, new FileReader("src/test/resources/schema.sql"));

        mockedDBUtils = Mockito.mockStatic(DBConnectionManager.class);
        mockedDBUtils.when(DBConnectionManager::getConnection).thenReturn(h2Connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (mockedDBUtils != null) mockedDBUtils.close();
        if (h2Connection != null) h2Connection.close();
    }

    // Hàm hỗ trợ tạo Tour giả để test
    private void createDummyTour(long tourId, String title) throws SQLException {
        String sql = "INSERT INTO tours (tour_id, title, price) VALUES (?, ?, '100$')";
        try (PreparedStatement pst = h2Connection.prepareStatement(sql)) {
            pst.setLong(1, tourId);
            pst.setString(2, title);
            pst.executeUpdate();
        }
    }

    // Hàm hỗ trợ tạo User giả
    private void createDummyUser(String userId, String email) throws SQLException {
        String sql = "INSERT INTO users (user_id, email, password) VALUES (?, ?, 'pass')";
        try (PreparedStatement pst = h2Connection.prepareStatement(sql)) {
            pst.setString(1, userId);
            pst.setString(2, email);
            pst.executeUpdate();
        }
    }

    @Test
    void testBookingProcess() throws SQLException {
        BookingsDAO dao = new BookingsDAO();

        // 1. CHUẨN BỊ DỮ LIỆU NỀN (Pre-condition)
        String userId = UUID.randomUUID().toString();
        long tourId = 999L;
        
        createDummyUser(userId, "booking_user@test.com");
        createDummyTour(tourId, "Amazing Ha Long Bay");

        // 2. THỰC HIỆN ĐẶT TOUR (Action)
        Bookings newBooking = new Bookings();
        newBooking.setUserId(userId);
        newBooking.setTourId(tourId);
        newBooking.setStartDate(java.sql.Date.valueOf("2023-12-01"));
        newBooking.setEndDate(java.sql.Date.valueOf("2023-12-05"));
        newBooking.setPrice("500.00");

        dao.bookings(newBooking);

        // 3. KIỂM TRA KẾT QUẢ (Assertion)
        // Lấy danh sách booking của user đó
        ArrayList<Bookings> userBookings = dao.getBookingsByUserId(userId);

        // Phải tìm thấy ít nhất 1 booking
        assertFalse(userBookings.isEmpty(), "Danh sách booking không được rỗng");
        assertEquals(1, userBookings.size());
        
        Bookings savedBooking = userBookings.get(0);
        assertEquals("Amazing Ha Long Bay", savedBooking.getTourTitle(), "Tên tour phải khớp");
        assertEquals("500.00", savedBooking.getPrice(), "Giá tiền phải khớp");
    }
}