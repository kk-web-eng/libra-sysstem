package com.zhoukai.librarysystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\",\"data\":null}");
            return false;
        }
        return true;
    }
}
