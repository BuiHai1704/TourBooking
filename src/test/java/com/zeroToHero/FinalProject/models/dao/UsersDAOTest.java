package com.zeroToHero.FinalProject.models.dao;

import com.zeroToHero.FinalProject.database.DBConnectionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersDAOTest {

    @Mock Connection mockConn;
    @Mock PreparedStatement mockPst;
    @Mock ResultSet mockRs;

    @Test
    void testCheckAvailableEmail_WhenEmailExists() throws SQLException {
        try (MockedStatic<DBConnectionManager> mockedDB = Mockito.mockStatic(DBConnectionManager.class)) {
            
            // 1. Cài đặt hành vi giả (Stubbing)
            // Khi gọi getConnection -> trả về mockConn
            mockedDB.when(DBConnectionManager::getConnection).thenReturn(mockConn);
            
            // Khi mockConn chuẩn bị statement -> trả về mockPst
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPst);
            
            // Khi execute query -> trả về mockRs (ResultSet giả)
            when(mockPst.executeQuery()).thenReturn(mockRs);
            
            // Giả lập DB trả về 1 dòng kết quả
            when(mockRs.next()).thenReturn(true);
            // Giả lập cột "exists" trong SQL trả về true
            when(mockRs.getBoolean("exists")).thenReturn(true);

            // 2. Gọi hàm cần test
            UsersDAO dao = new UsersDAO();
            boolean exists = dao.checkAvailableEmail("existing@gmail.com");

            // 3. Kiểm tra kết quả
            assertTrue(exists, "Nếu DB trả về true, hàm phải trả về true");
        }
        // Kết thúc khối try, DBConnectionManager trở lại bình thường
    }
}