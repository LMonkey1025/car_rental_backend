package com.example.car_rental_project.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.car_rental_project.model.dto.user.UserCert; // 請確保您有這個 UserCert DTO
import com.example.car_rental_project.model.entity.Role;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
@Order(1)
public class RootAccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request; // 將 ServletRequest 轉換為 HttpServletRequest
        HttpServletResponse httpResponse = (HttpServletResponse) response; // 將 ServletResponse 轉換為 HttpServletResponse

        // 允許 OPTIONS 請求通過（CORS 預檢請求）
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (httpRequest.getRequestURI().startsWith(httpRequest.getContextPath() + "/root/")) {
            HttpSession session = httpRequest.getSession(false); // false 表示如果 session 不存在則不建立新的
            UserCert userCert = null;

            if (session != null) {
                // 假設您在登入成功後，將 UserCert 物件以 "userCert" 為鍵名存入 Session
                userCert = (UserCert) session.getAttribute("userCert");
            }

            if (userCert == null || userCert.getRole() != Role.ADMIN) {
                // 如果使用者未登入 (session 中沒有 userCert)，或者角色不是 ADMIN
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
                // 您可以自訂錯誤回應的內容類型和訊息
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter()
                        .write("{\"message\": \"沒有權限使用此API.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

}
