package com.electricity.backend.config;

import com.electricity.backend.entity.SessionToken;
import com.electricity.backend.repository.SessionTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionTokenRepository tokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // CORS preflight requests should be bypassed
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Bypass auth routes and H2 console
        if (path.startsWith("/api/auth/") || path.startsWith("/h2-console") || path.startsWith("/error")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Missing or invalid Authorization header.\"}");
            return false;
        }

        String tokenStr = authHeader.substring(7);
        SessionToken sessionToken = tokenRepository.findByToken(tokenStr).orElse(null);

        if (sessionToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid session token.\"}");
            return false;
        }

        if (sessionToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(sessionToken);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Session expired. Please log in again.\"}");
            return false;
        }

        // Set user in request context
        request.setAttribute("currentUser", sessionToken.getUser());

        // Role-based path authorization
        String userRole = sessionToken.getUser().getRole();
        if (path.startsWith("/api/admin/") && !"ADMIN".equalsIgnoreCase(userRole)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Access denied. Admin role required.\"}");
            return false;
        }

        if (path.startsWith("/api/sme/") && !"SME".equalsIgnoreCase(userRole) && !"ADMIN".equalsIgnoreCase(userRole)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Access denied. SME role required.\"}");
            return false;
        }

        if (path.startsWith("/api/customer/") && !"CUSTOMER".equalsIgnoreCase(userRole)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Access denied. Customer role required.\"}");
            return false;
        }

        return true;
    }
}
