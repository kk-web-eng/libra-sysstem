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
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");
        SysUser user = sysUserService.login(username, password);
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("realName", user.getRealName());

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        return Result.ok(data);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    @GetMapping("/currentUser")
    public Result<?> currentUser(HttpSession session) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", session.getAttribute("userId"));
        data.put("username", session.getAttribute("username"));
        data.put("realName", session.getAttribute("realName"));
        return Result.ok(data);
    }
}
