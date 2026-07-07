package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.ReaderInfo;
import com.zhoukai.librarysystem.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @GetMapping
    public Result<Page<ReaderInfo>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(readerService.page(current, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<ReaderInfo> get(@PathVariable Long id) {
        return Result.ok(readerService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody ReaderInfo reader) {
        readerService.save(reader);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody ReaderInfo reader) {
        readerService.update(reader);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        readerService.delete(id);
        return Result.ok();
    }
}
