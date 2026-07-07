package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.BookInfo;
import com.zhoukai.librarysystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public Result<Page<BookInfo>> books(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.ok(bookService.page(current, size, keyword, category));
    }

    @GetMapping("/categories")
    public Result<?> categories() {
        return Result.ok(bookService.categories());
    }
}
