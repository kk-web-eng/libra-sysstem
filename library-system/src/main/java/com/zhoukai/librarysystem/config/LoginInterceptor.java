package com.zhoukai.librarysystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
/** 在管理员接口执行前检查是否已经登录。 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // getSession(false) 只读取已有 Session，不会为未登录请求创建新 Session。
        HttpSession session = request.getSession(false);

        // 核心流程：管理员登录成功后，LoginController 会把 userId 存入 Session。
        // 这里通过 userId 判断登录状态；没有登录时直接结束请求，不再进入 Controller。
        if (session == null || session.getAttribute("userId") == null) {
            response.setContentType("application/json;charset=UTF-8");
            // 项目把业务状态放在 JSON 的 code 中，所以 HTTP 状态仍返回 200。
            response.setStatus(200);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\",\"data\":null}");
            return false;
        }
        // 返回 true 表示允许请求继续执行。
        return true;
    }
}
