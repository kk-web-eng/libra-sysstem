package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.ReaderInfo;
import com.zhoukai.librarysystem.mapper.ReaderInfoMapper;
import com.zhoukai.librarysystem.service.BorrowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserPortalController {

    @Autowired
    private ReaderInfoMapper readerInfoMapper;
    @Autowired
    private BorrowService borrowService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> body) {
        String account = text(body.get("account"));
        String password = text(body.get("password"));
        if (account.length() < 6) return Result.fail("账号至少 6 位");
        if (password.isBlank()) return Result.fail("请输入密码");
        if (readerInfoMapper.selectCount(new LambdaQueryWrapper<ReaderInfo>().eq(ReaderInfo::getReaderNo, account)) > 0) {
            return Result.fail("账号已存在");
        }
        ReaderInfo reader = new ReaderInfo();
        reader.setReaderNo(account);
        reader.setPassword(password);
        reader.setName("用户" + account.substring(Math.max(0, account.length() - 4)));
        reader.setPhone("");
        reader.setStatus(1);
        readerInfoMapper.insert(reader);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        ReaderInfo reader = readerInfoMapper.selectOne(new LambdaQueryWrapper<ReaderInfo>()
                .eq(ReaderInfo::getReaderNo, text(body.get("account")))
                .eq(ReaderInfo::getPassword, text(body.get("password")))
                .eq(ReaderInfo::getStatus, 1));
        if (reader == null) return Result.fail("账号或密码错误");
        session.setAttribute("readerId", reader.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("id", reader.getId());
        data.put("account", reader.getReaderNo());
        data.put("name", reader.getName());
        data.put("phone", reader.getPhone());
        return Result.ok(data);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.removeAttribute("readerId");
        return Result.ok();
    }

    @PostMapping("/borrow/{bookId}")
    public Result<?> borrow(@PathVariable Long bookId, @RequestBody(required = false) Map<String, String> body, HttpSession session) {
        Long readerId = currentReaderId(session);
        borrowService.borrowByReader(bookId, readerId, parseDate(body));
        return Result.ok();
    }

    @GetMapping("/borrows")
    public Result<?> borrows(@RequestParam(defaultValue = "1") int current,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) Integer status,
                             HttpSession session) {
        Long readerId = currentReaderId(session);
        return Result.ok(borrowService.page(current, size, null, readerId, status, null));
    }

    @PostMapping("/renew/{id}")
    public Result<?> renew(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body, HttpSession session) {
        borrowService.renew(id, currentReaderId(session), parseDate(body));
        return Result.ok();
    }

    private Long currentReaderId(HttpSession session) {
        Object id = session.getAttribute("readerId");
        if (id == null) throw new RuntimeException("请先登录用户账号");
        return (Long) id;
    }

    private LocalDate parseDate(Map<String, String> body) {
        if (body == null || body.get("dueDate") == null || body.get("dueDate").isBlank()) return null;
        return LocalDate.parse(body.get("dueDate"));
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
