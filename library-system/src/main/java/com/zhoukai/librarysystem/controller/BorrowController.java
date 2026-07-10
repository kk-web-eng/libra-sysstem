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
/** 处理管理员端的借阅查询、登记、归还和续借。 */
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @GetMapping
    // 支持按图书、用户、状态和关键词组合查询借阅记录。
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
        // operatorId 记录是哪位管理员完成了这次借书登记。
        Long operatorId = (Long) session.getAttribute("userId");
        borrowService.borrow(body, operatorId);
        return Result.ok();
    }

    @PostMapping("/return/{id}")
    // 归还图书，Service 会同时修改记录状态和可借库存。
    public Result<?> returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return Result.ok();
    }

    @PostMapping("/renew/{id}")
    // 管理员为借阅记录设置新的应还日期。
    public Result<?> renew(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        borrowService.renew(id, null, parseDate(body));
        return Result.ok();
    }

    private LocalDate parseDate(Map<String, String> body) {
        // 没传日期时返回 null，Service 会使用默认续借日期。
        if (body == null || body.get("dueDate") == null || body.get("dueDate").isBlank()) return null;
        return LocalDate.parse(body.get("dueDate"));
    }
}
