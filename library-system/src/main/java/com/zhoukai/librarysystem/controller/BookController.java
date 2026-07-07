package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.BookInfo;
import com.zhoukai.librarysystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public Result<Page<BookInfo>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.ok(bookService.page(current, size, keyword, category));
    }

    @GetMapping("/categories")
    public Result<?> categories() {
        return Result.ok(bookService.categories());
    }

    @GetMapping("/{id}")
    public Result<BookInfo> get(@PathVariable Long id) {
        return Result.ok(bookService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody BookInfo book) {
        bookService.save(book);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody BookInfo book) {
        bookService.update(book);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.ok();
    }
}
