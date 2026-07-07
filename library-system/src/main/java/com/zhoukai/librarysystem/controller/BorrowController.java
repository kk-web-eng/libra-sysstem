package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import com.zhoukai.librarysystem.service.BorrowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @GetMapping
    public Result<Page<BorrowRecord>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long readerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.ok(borrowService.page(current, size, bookId, readerId, status, keyword));
    }

    @PostMapping("/borrow")
    public Result<?> borrow(@RequestBody Map<String, Object> body, HttpSession session) {
        Long operatorId = (Long) session.getAttribute("userId");
        borrowService.borrow(body, operatorId);
        return Result.ok();
    }

    @PostMapping("/return/{id}")
    public Result<?> returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return Result.ok();
    }

    @PostMapping("/renew/{id}")
    public Result<?> renew(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        borrowService.renew(id, null, parseDate(body));
        return Result.ok();
    }

    private LocalDate parseDate(Map<String, String> body) {
        if (body == null || body.get("dueDate") == null || body.get("dueDate").isBlank()) return null;
        return LocalDate.parse(body.get("dueDate"));
    }
}
