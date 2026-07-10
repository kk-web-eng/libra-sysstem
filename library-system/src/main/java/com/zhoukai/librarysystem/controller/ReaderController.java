package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.ReaderInfo;
import com.zhoukai.librarysystem.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readers")
/** 提供后台用户资料的增删改查接口。 */
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @GetMapping
    // 按姓名或账号分页查询用户。
    public Result<Page<ReaderInfo>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(readerService.page(current, size, keyword));
    }

    @GetMapping("/{id}")
    // 读取单个用户资料。
    public Result<ReaderInfo> get(@PathVariable Long id) {
        return Result.ok(readerService.getById(id));
    }

    @PostMapping
    // 新增用户，账号规则由 Service 校验。
    public Result<?> add(@RequestBody ReaderInfo reader) {
        readerService.save(reader);
        return Result.ok();
    }

    @PutMapping
    // 修改用户资料和启用状态。
    public Result<?> update(@RequestBody ReaderInfo reader) {
        readerService.update(reader);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    // 删除用户；有未归还图书时不允许删除。
    public Result<?> delete(@PathVariable Long id) {
        readerService.delete(id);
        return Result.ok();
    }
}
