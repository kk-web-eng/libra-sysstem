package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.SysUser;
import com.zhoukai.librarysystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    public Result<Page<SysUser>> list(@RequestParam(defaultValue = "1") int current,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String keyword) {
        return Result.ok(sysUserService.page(current, size, keyword));
    }

    @PostMapping
    public Result<?> add(@RequestBody SysUser user) {
        sysUserService.save(user);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody SysUser user) {
        sysUserService.update(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.ok();
    }
}
