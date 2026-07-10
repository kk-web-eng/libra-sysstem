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
/** 提供不登录也能访问的公共馆藏查询接口。 */
public class PublicController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    // 查询公开图书目录，默认每页 12 本。
    public Result<Page<BookInfo>> books(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.ok(bookService.page(current, size, keyword, category));
    }

    @GetMapping("/categories")
    // 给公共页面提供分类筛选选项。
    public Result<?> categories() {
        return Result.ok(bookService.categories());
    }
}
