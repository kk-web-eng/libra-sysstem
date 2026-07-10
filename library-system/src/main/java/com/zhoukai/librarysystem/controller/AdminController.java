package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.SysUser;
import com.zhoukai.librarysystem.service.SysUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
/** 维护后台管理员账号；全部接口只允许系统管理员调用。 */
public class AdminController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    // 分页查询管理员账号。
    public Result<Page<SysUser>> list(@RequestParam(defaultValue = "1") int current,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String keyword,
                                      HttpSession session) {
        if (!isSuperAdmin(session)) return Result.fail(403, "只有系统管理员可以管理管理员账号");
        return Result.ok(sysUserService.page(current, size, keyword));
    }

    @PostMapping
    // 新增管理员。
    public Result<?> add(@RequestBody SysUser user, HttpSession session) {
        if (!isSuperAdmin(session)) return Result.fail(403, "只有系统管理员可以管理管理员账号");
        sysUserService.save(user);
        return Result.ok();
    }

    @PutMapping
    // 修改管理员信息、等级或启用状态。
    public Result<?> update(@RequestBody SysUser user, HttpSession session) {
        if (!isSuperAdmin(session)) return Result.fail(403, "只有系统管理员可以管理管理员账号");
        sysUserService.update(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    // 删除指定管理员。
    public Result<?> delete(@PathVariable Long id, HttpSession session) {
        if (!isSuperAdmin(session)) return Result.fail(403, "只有系统管理员可以管理管理员账号");
        sysUserService.delete(id);
        return Result.ok();
    }

    private boolean isSuperAdmin(HttpSession session) {
        // 权限必须在后端再次判断。
        // 前端隐藏菜单只能改善界面体验，不能阻止用户手动调用 /api/admins。
        Object role = session.getAttribute("role");
        Object username = session.getAttribute("username");
        // admin 账号作为兼容条件，避免旧数据中角色字段未更新时失去系统管理权限。
        return "SUPER_ADMIN".equals(role) || "admin".equals(username);
    }
}
