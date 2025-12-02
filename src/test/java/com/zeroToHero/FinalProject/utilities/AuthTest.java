package com.zeroToHero.FinalProject.utilities;

import com.zeroToHero.FinalProject.models.beans.Users;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthTest {

    @Mock
    HttpServletResponse response; // Giả lập đối tượng Response

    @Test
    void testPasswordFlow() {
        // 1. Giả lập input
        String rawPassword = "mySecretPassword";
        
        // 2. Thực hiện hành động: Mã hóa
        String encrypted = Auth.encryptPassword(rawPassword);
        
        // 3. Kiểm tra (Assert)
        assertNotNull(encrypted);
        assertNotEquals(rawPassword, encrypted);

        // 4. Kiểm tra logic check password
        assertTrue(Auth.checkPassword(rawPassword, encrypted), "Mật khẩu đúng phải trả về true");
        assertFalse(Auth.checkPassword("wrongPassword", encrypted), "Mật khẩu sai phải trả về false");
    }

    @Test
    void testSetTokenCookies() {
        // 1. Chuẩn bị data giả
        Users mockUser = new Users();
        mockUser.setEmail("test@gmail.com");
        // Set thêm các field cần thiết khác để tránh lỗi null pointer trong Auth nếu có...

        // 2. Gọi hàm cần test (truyền vào response giả)
        Auth.setTokenCookies(response, mockUser);

        // 3. Kiểm tra xem response.addCookie() có được gọi 2 lần không (cho 2 cookie)
        // ArgumentCaptor giúp bắt lấy đối tượng Cookie được truyền vào hàm addCookie
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieCaptor.capture());

        // 4. Kiểm tra chi tiết Cookie bắt được
        Cookie tokenHeaderCookie = cookieCaptor.getAllValues().get(0);
        assertEquals("tokenHeaderPayload", tokenHeaderCookie.getName());
        
        Cookie signatureCookie = cookieCaptor.getAllValues().get(1);
        assertEquals("tokenSignature", signatureCookie.getName());
        assertTrue(signatureCookie.isHttpOnly());
    }
}