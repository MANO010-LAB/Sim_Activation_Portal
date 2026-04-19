package com.example.sim_activation_portal.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // Allow login/logout and static resources
        if (uri.equals("/api/auth/login") || uri.equals("/api/auth/logout") || uri.startsWith("/css/") || uri.startsWith("/js/")) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.setStatus(401);
            response.getWriter().write("Unauthorized: Please login");
            return false;
        }
        return true;
    }
}