package com.zeroToHero.FinalProject.servlets;

import com.zeroToHero.FinalProject.models.beans.Bookings;
import com.zeroToHero.FinalProject.models.dao.BookingsDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingsServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;

    @InjectMocks
    BookingsServlet bookingsServlet;

    @Test
    void testDoPost_SuccessfulBooking() throws Exception {
        // 1. Giả lập dữ liệu gửi lên từ form
        when(request.getParameter("userId")).thenReturn("user-123");
        when(request.getParameter("tourId")).thenReturn("10");
        when(request.getParameter("price")).thenReturn("500.0");
        when(request.getParameter("checkInDate")).thenReturn("2023-12-01");
        when(request.getParameter("checkOutDate")).thenReturn("2023-12-05");
        when(request.getContextPath()).thenReturn("/myapp");

        // 2. Mock Constructor của BookingsDAO
        // Bất kỳ khi nào "new BookingsDAO()" được gọi, nó sẽ trả về mockDAO này
        try (MockedConstruction<BookingsDAO> mockedDAO = Mockito.mockConstruction(BookingsDAO.class)) {
            
            // 3. Gọi hàm cần test
            bookingsServlet.doPost(request, response);

            // 4. Lấy đối tượng DAO mock đã được tạo ra bên trong Servlet
            BookingsDAO daoInstance = mockedDAO.constructed().get(0);

            // 5. Verify: Đảm bảo hàm bookings() của DAO đã được gọi với đúng dữ liệu
            verify(daoInstance).bookings(any(Bookings.class));
            
            // Verify: Đảm bảo chuyển hướng người dùng sau khi book xong
            verify(response).sendRedirect("/myapp/me");
        }
    }
}