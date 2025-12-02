package com.zeroToHero.FinalProject.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFilterTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain chain;

    @InjectMocks
    AdminFilter adminFilter;

    @Test
    void testDoFilter_IsAdmin() throws Exception {
        // Giả lập session attributes báo hiệu là Admin
        when(request.getAttribute("login")).thenReturn(true);
        when(request.getAttribute("admin")).thenReturn(true);

        adminFilter.doFilter(request, response, chain);

        // Mong đợi: Cho phép đi tiếp (chain.doFilter được gọi)
        verify(chain, times(1)).doFilter(request, response);
        // Mong đợi: KHÔNG bị chuyển hướng
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testDoFilter_NotAdmin() throws Exception {
        // Giả lập là User thường (admin = false)
        when(request.getAttribute("login")).thenReturn(true);
        when(request.getAttribute("admin")).thenReturn(false);
        when(request.getContextPath()).thenReturn("/myapp");

        adminFilter.doFilter(request, response, chain);

        // Mong đợi: Bị đá về trang chủ
        verify(response).sendRedirect("/myapp/");
        // Mong đợi: Filter chain dừng lại
        verify(chain, never()).doFilter(request, response);
    }
    
    @Test
    void testDoFilter_NotLoggedIn() throws Exception {
        // Giả lập chưa đăng nhập (attribute null)
        when(request.getAttribute("login")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/myapp");

        adminFilter.doFilter(request, response, chain);

        verify(response).sendRedirect("/myapp/");
    }
}