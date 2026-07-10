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
/** 处理普通用户注册、登录、自助借书、借阅查询和续借。 */
public class UserPortalController {

    @Autowired
    private ReaderInfoMapper readerInfoMapper;
    @Autowired
    private BorrowService borrowService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> body) {
        // 注册只接收账号和密码，姓名使用账号末尾自动生成。
        String account = text(body.get("account"));
        String password = text(body.get("password"));
        if (account.length() < 6) return Result.fail("账号至少 6 位");
        if (password.isBlank()) return Result.fail("请输入密码");
        if (readerInfoMapper.selectCount(new LambdaQueryWrapper<ReaderInfo>().eq(ReaderInfo::getReaderNo, account)) > 0) {
            return Result.fail("账号已存在");
        }
        // readerNo 同时作为读者账号，数据库中的唯一索引会避免重复。
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
        // 必须同时匹配账号、密码和启用状态。
        ReaderInfo reader = readerInfoMapper.selectOne(new LambdaQueryWrapper<ReaderInfo>()
                .eq(ReaderInfo::getReaderNo, text(body.get("account")))
                .eq(ReaderInfo::getPassword, text(body.get("password")))
                .eq(ReaderInfo::getStatus, 1));
        if (reader == null) return Result.fail("账号或密码错误");
        // 读者端单独保存 readerId，不与后台管理员的 userId 混用。
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
        // 只移除读者身份，不影响同一浏览器中的管理员 Session 数据。
        session.removeAttribute("readerId");
        return Result.ok();
    }

    @PostMapping("/borrow/{bookId}")
    public Result<?> borrow(@PathVariable Long bookId, @RequestBody(required = false) Map<String, String> body, HttpSession session) {
        // 从 Session 取当前读者，防止前端冒充其他 readerId 借书。
        Long readerId = currentReaderId(session);
        borrowService.borrowByReader(bookId, readerId, parseDate(body));
        return Result.ok();
    }

    @GetMapping("/borrows")
    // 只查询当前登录用户自己的借阅记录。
    public Result<?> borrows(@RequestParam(defaultValue = "1") int current,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) Integer status,
                             HttpSession session) {
        Long readerId = currentReaderId(session);
        return Result.ok(borrowService.page(current, size, null, readerId, status, null));
    }

    @PostMapping("/renew/{id}")
    // 读者续借；Service 还会检查记录是否属于当前读者。
    public Result<?> renew(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body, HttpSession session) {
        borrowService.renew(id, currentReaderId(session), parseDate(body));
        return Result.ok();
    }

    private Long currentReaderId(HttpSession session) {
        // 所有需要读者身份的接口都通过此方法进行统一检查。
        Object id = session.getAttribute("readerId");
        if (id == null) throw new RuntimeException("请先登录用户账号");
        return (Long) id;
    }

    private LocalDate parseDate(Map<String, String> body) {
        // 前端使用 YYYY-MM-DD，LocalDate.parse 可以直接转换。
        if (body == null || body.get("dueDate") == null || body.get("dueDate").isBlank()) return null;
        return LocalDate.parse(body.get("dueDate"));
    }

    private String text(String value) {
        // 把 null 变为空字符串，并去掉用户输入首尾空格。
        return value == null ? "" : value.trim();
    }
}
