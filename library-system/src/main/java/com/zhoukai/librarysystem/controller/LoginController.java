package com.zhoukai.librarysystem.controller;

import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.SysUser;
import com.zhoukai.librarysystem.service.SysUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
/** 处理后台管理员的登录、退出和当前账号查询。 */
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        // 从前端提交的 JSON 中取得账号和密码。
        String username = body.get("username");
        String password = body.get("password");
        SysUser user = sysUserService.login(username, password);
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        // 登录成功后把身份保存到 Session。
        // 后续请求会自动携带 Session Cookie，拦截器和权限接口由此识别当前管理员。
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("realName", user.getRealName());
        session.setAttribute("role", user.getRole());

        // 只返回前端需要的账号信息，不把密码返回给浏览器。
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        return Result.ok(data);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        // invalidate 会清除当前 Session 中的全部登录信息。
        session.invalidate();
        return Result.ok();
    }

    @GetMapping("/currentUser")
    public Result<?> currentUser(HttpSession session) {
    // 页面刷新后可以从后端重新取得当前登录身份。
        Map<String, Object> data = new HashMap<>();
        data.put("userId", session.getAttribute("userId"));
        data.put("username", session.getAttribute("username"));
        data.put("realName", session.getAttribute("realName"));
        data.put("role", session.getAttribute("role"));
        return Result.ok(data);
    }
}
