package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.BookInfo;
import com.zhoukai.librarysystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
/** 提供后台图书的查询、新增、修改和删除接口。 */
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    // 按关键词、分类和分页参数查询图书。
    public Result<Page<BookInfo>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.ok(bookService.page(current, size, keyword, category));
    }

    @GetMapping("/categories")
    // 返回数据库中已有的图书分类。
    public Result<?> categories() {
        return Result.ok(bookService.categories());
    }

    @GetMapping("/{id}")
    // 根据主键查询一本图书。
    public Result<BookInfo> get(@PathVariable Long id) {
        return Result.ok(bookService.getById(id));
    }

    @PostMapping
    // 新增图书。
    public Result<?> add(@RequestBody BookInfo book) {
        bookService.save(book);
        return Result.ok();
    }

    @PutMapping
    // 修改图书信息和馆藏总数。
    public Result<?> update(@RequestBody BookInfo book) {
        bookService.update(book);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    // 删除图书；Service 会先检查是否存在未归还记录。
    public Result<?> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.ok();
    }
}
