package com.zeroToHero.FinalProject.servlets;

import com.zeroToHero.FinalProject.models.beans.Users;
import com.zeroToHero.FinalProject.models.dao.UsersDAO;
import jakarta.servlet.RequestDispatcher;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock RequestDispatcher dispatcher;

    @InjectMocks
    SignUpServlet signUpServlet;

    @Test
    void testDoPost_SignUpSuccess() throws Exception {
        // 1. Giả lập input (CHỈ giả lập những gì Servlet thực sự gọi)
        // Servlet SignUpServlet không gọi getParameter("country"), nên không được stub nó.
        when(request.getParameter("firstName")).thenReturn("John");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("email")).thenReturn("newuser@test.com");
        when(request.getParameter("password")).thenReturn("123456");

        // 2. Mock UsersDAO
        try (MockedConstruction<UsersDAO> mocked = Mockito.mockConstruction(UsersDAO.class,
                (mock, context) -> {
                    // checkAvailableEmail trả về false (chưa tồn tại) -> Cho phép đăng ký
                    when(mock.checkAvailableEmail(anyString())).thenReturn(false);
                })) {

            signUpServlet.doPost(request, response);

            UsersDAO dao = mocked.constructed().get(0);
            verify(dao).signUp(any(Users.class)); // Verify hàm signUp được gọi
            verify(response).sendRedirect("log-in"); // Verify chuyển hướng đúng
        }
    }

    @Test
    void testDoPost_SignUpFail_EmailExists() throws Exception {
        // 1. Stub đầy đủ các tham số được gọi trước khi check email
        when(request.getParameter("firstName")).thenReturn("John");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("email")).thenReturn("exist@test.com");
        when(request.getParameter("password")).thenReturn("123456");
        
        // Mock Dispatcher để verify lệnh forward
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        try (MockedConstruction<UsersDAO> mocked = Mockito.mockConstruction(UsersDAO.class,
                (mock, context) -> {
                    // checkAvailableEmail trả về true (đã tồn tại) -> Báo lỗi
                    when(mock.checkAvailableEmail("exist@test.com")).thenReturn(true);
                })) {

            signUpServlet.doPost(request, response);

            UsersDAO dao = mocked.constructed().get(0);
            
            // Verify: KHÔNG được gọi signUp
            verify(dao, never()).signUp(any(Users.class));
            
            // Verify: Phải set attribute lỗi và FORWARD về trang sign_up.jsp (không phải redirect)
            verify(request).setAttribute("error", true);
            verify(request).getRequestDispatcher("/WEB-INF/views/sign_up.jsp");
            verify(dispatcher).forward(request, response);
        }
    }
}